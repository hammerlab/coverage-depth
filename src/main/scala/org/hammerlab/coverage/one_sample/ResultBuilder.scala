package org.hammerlab.coverage.one_sample

import org.apache.spark.rdd.RDD
import org.hammerlab.coverage.IsKey
import org.hammerlab.coverage.histogram.JointHistogram
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import org.hammerlab.genomics.reference.NumLoci
import org.hammerlab.math.Steps.roundNumbers
import spire.algebra.Monoid
import org.hammerlab.magic.rdd.size._
import org.hammerlab.math.ceil

import scala.math.max
import scala.reflect.ClassTag

abstract class ResultBuilder[K <: Key[C] : ClassTag : IsKey, C: Monoid : ClassTag, Result]
  extends Serializable {

  def make(jh: JointHistogram,
           pdf: PDF[C],
           cdf: CDF[C],
           filteredCDF: Array[(Depth, C)],
           maxDepth: Depth,
           firstCounts: C,
           totalReferenceLoci: NumLoci): Result

  def make(jh: JointHistogram, totalReferenceLoci: NumLoci): Result = {
    val ik = implicitly[IsKey[K]]

    val j = jh.jh
    val keys = j.map(ik.make)

    val m = implicitly[Monoid[C]]

    val depthSums =
      keys
        .map(
          key ⇒
            key.depth →
              key.toCounts
        )
        .reduceByKey(m.op)

    val numDepths = depthSums.size
    val depthsPerPartition = 1000
    val numPartitions = ceil(numDepths, depthsPerPartition).toInt

    val pdf: PDF[C] =
      new PDF {
        override def rdd: RDD[(Depth, C)] =
          depthSums
            .sortByKey(numPartitions = numPartitions)
      }

    val cdf = pdf.cdf

    val maxDepth = pdf.rdd.keys.reduce(max)

    val depthSteps = roundNumbers(maxDepth)

    val stepsBC = jh.sc.broadcast(depthSteps)

    val filteredCDF =
      (for {
        (depth, count) ← cdf
        depthFilter = stepsBC.value
        if depthFilter(depth)
      } yield
        depth → count
      )
      .collect
      .sortBy(_._1)

    val (firstDepth, firstCounts) = filteredCDF.take(1)(0)
    if (firstDepth > 1) {
      throw new Exception(s"Bad first firstDepth: $firstDepth (count: $firstCounts)")
    }

    make(jh, pdf, cdf, filteredCDF, maxDepth, firstCounts, totalReferenceLoci)
  }
}
