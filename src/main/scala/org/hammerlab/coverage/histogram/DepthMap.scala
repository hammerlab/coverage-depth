package org.hammerlab.coverage.histogram

import grizzled.slf4j.Logging
import org.apache.hadoop.io.compress.{ CompressionCodec, GzipCodec }
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.bdgenomics.adam.projections.{ FeatureField, Projection }
import org.bdgenomics.adam.rdd.ADAMContext._
import org.bdgenomics.adam.rdd.feature.FeatureRDD
import org.bdgenomics.formats.avro.AlignmentRecord
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import org.hammerlab.genomics.reference.ContigName.Factory
import org.hammerlab.genomics.reference.{ ContigName, Locus, Position ⇒ Pos }
import org.hammerlab.math.ceil
import org.hammerlab.paths.Path

case class DepthMap(rdd: DepthMap.T) {
  def save(path: Path,
           codec: Class[_ <: CompressionCodec] = classOf[GzipCodec]): DepthMap = {
    (
      for {
        (Pos(contigName, locus), depth) ← rdd
      } yield
        s"$contigName\t$locus\t$depth"
    )
    .saveAsTextFile(
      path.toString(),
      codec
    )

    this
  }
}

object DepthMap
  extends Logging {

  type T = RDD[(Pos, Depth)]

  def apply(reads: RDD[AlignmentRecord])(implicit factory: Factory): DepthMap = {
    val rdd =
      for {
        read ← reads if read.getReadMapped
        contigName ← Option(read.getContigName: ContigName).toList
        start ← Option(Locus(read.getStart)).toList
        end ← Option(Locus(read.getEnd)).toList
        refLen = (end - start).toInt
        i ← 0 until refLen
      } yield
        Pos(contigName, start + i) → 1

    DepthMap(
      rdd
        .reduceByKey(_ + _)
    )
  }

  def apply(path: Path,
            bytesPerIntervalPartition: Int,
            dedupeLoci: Boolean = true,
            writeDepths: Boolean = true)(
      implicit
      sc: SparkContext,
      factory: Factory
  ): DepthMap =
    path.extension match {
      case "depths" ⇒
        DepthMap(
          sc
            .textFile(path.toString())
            .map {
              line ⇒
                line.split("\t") match {
                  case Array(contigName, locus, depth) ⇒
                    Pos(
                      contigName,
                      Locus(locus.toLong)
                    ) →
                      depth.toInt
                  case _ ⇒
                    throw new IllegalArgumentException(
                      s"Bad depth-map line: $line"
                    )
              }
            }
        )
      case _ ⇒

        val depthsPath = path + ".depths"
        if (depthsPath.exists)
          apply(
            depthsPath,
            bytesPerIntervalPartition,
            dedupeLoci
          )
        else {
          val featuresProjection =
            Projection(
              FeatureField.contigName,
              FeatureField.start,
              FeatureField.end
            )

          val fileLength = path.size
          val numPartitions = ceil(fileLength, bytesPerIntervalPartition).toInt
          info(s"Loading interval file $path of size $fileLength using $numPartitions partitions")
          apply(
            sc.loadFeatures(
              path,
              optStorageLevel = None,
              Some(featuresProjection),
              Some(numPartitions)
            ),
            dedupeLoci
          )
          .save(depthsPath)
        }
    }

  def apply(features: FeatureRDD,
            dedupeLoci: Boolean)(implicit factory: Factory): DepthMap = {
    val lociCounts: RDD[Pos] =
      for {
        feature ← features.rdd
        contigName ← Option(feature.getContigName: ContigName).toList
        start ← Option(Locus(feature.getStart)).toList
        end ← Option(Locus(feature.getEnd)).toList
        refLen = (end - start).toInt
        i ← 0 until refLen
      } yield
        Pos(contigName, start + i)

    DepthMap(
      if (dedupeLoci)
        lociCounts
          .distinct
          .map(_ → 1)
      else
        lociCounts
          .map(_ → 1)
          .reduceByKey(_ + _)
    )
  }

  implicit def unwrap(depthMap: DepthMap): DepthMap.T = depthMap.rdd
}
