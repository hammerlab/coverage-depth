package org.hammerlab.coverage

import hammerlab.cli._
import hammerlab.path._
import org.hammerlab.coverage.histogram.JointHistogram.{ fromPaths, load }
import org.hammerlab.coverage.kryo.Registrar
import org.hammerlab.genomics.readsets.ReadSets
import org.hammerlab.genomics.readsets.args.path.UnprefixedPath
import org.hammerlab.genomics.readsets.args.{ Base, NoSequenceDictionaryArgs, PathPrefixArg }
import org.hammerlab.genomics.readsets.io.{ ReadFilterArgs, Sample }
import org.hammerlab.genomics.reference.ContigName.Normalization.Lenient
import org.hammerlab.hadoop.splits.MaxSplitSize

object Main
  extends Cmd {

  case class Opts(

    @R readFilterArgs: ReadFilterArgs,
    @R noSequenceDictionaryArgs: NoSequenceDictionaryArgs,

    paths: Array[Path],
    sampleNames: Array[Sample.Name],

    @R pathPrefixArg: PathPrefixArg,

    @O("o")
    @M("Directory to write results to")
    out: UnprefixedPath,

    @O("f")
    @M("Write result files even if they already exist")
    force: Boolean = false,

    @O("jh")
    @M("When set, save the computed joint-histogram; if one already exists, skip reading it, recompute it, and overwrite it")
    persistJointHistogram: Boolean = false,

    @O("i")
    @M("Intervals file or capture kit; print stats for loci matching this intervals file, not matching, and total.")
    intervalsFile: Option[UnprefixedPath] = None,

    @O("b")
    @M("Number of bytes per chunk of input interval-file")
    intervalPartitionBytes: Int = 1 << 16,

    @O("v")
    @M("When set, persist full PDF and CDF of coverage-depth histogram")
    persistDistributions: Boolean = false
  )
  extends Base

  val main = Main(
    new spark.App(_, Registrar) {

      implicit val prefix = opts.pathPrefixArg.dir
      val out: Path = opts.out

      val (readsets, _) = ReadSets(opts)

      val contigLengths = readsets.contigLengths
      val totalReferenceLoci = contigLengths.totalLength

      val force = opts.force
      val forceStr = if (force) " (forcing)" else ""

      val intervalsFile = opts.intervalsFile.map(_.buildPath)

      val intervalsPathStr =
        intervalsFile
          .fold {
            ""
          } {
            path ⇒ s"against $path "
          }

      val jointHistogramPath = getJointHistogramPath(out)

      val jointHistogramPathExists = jointHistogramPath.exists

      val writeJointHistogram = opts.persistJointHistogram

      implicit val splitSize = MaxSplitSize(opts.readFilterArgs.splitSize)

      val jh =
        if (!writeJointHistogram && jointHistogramPathExists) {
          info(s"Loading JointHistogram: $jointHistogramPath")
          load(sc, jointHistogramPath)
        } else {
          info(
            s"Analyzing ${opts.paths.mkString("(", ", ", ")")} ${intervalsPathStr}and writing to $out$forceStr"
          )
          fromPaths(
            opts.paths,
            intervalsFile.toList,
            bytesPerIntervalPartition = opts.intervalPartitionBytes
          )
        }

      opts.paths.length match {
        case 1 ⇒
          (
            if(intervalsFile.isDefined)
              one_sample.with_intervals.ResultBuilder
            else
              one_sample.without_intervals.ResultBuilder
          )
          .make(jh, totalReferenceLoci)
          .save(
            out,
            force = force,
            writeFullDistributions = opts.persistDistributions,
            writeJointHistogram = writeJointHistogram
          )
        case 2 ⇒
          (if (intervalsFile.isDefined)
            two_sample.with_intervals.ResultBuilder
          else
            two_sample.without_intervals.ResultBuilder
            )
            .make(jh, totalReferenceLoci)
            .save(
              out,
              force = force,
              writeFullDistributions = opts.persistDistributions,
              writeJointHistogram = writeJointHistogram
            )
        case num ⇒
          throw new IllegalArgumentException(
            s"Maximum of two reads-sets allowed; found $num: ${opts.paths.mkString(",")}"
          )
      }
    }
  )

//  override def description: String =
//    "Given one or two sets of reads, and an optional set of intervals, compute a joint histogram over the reads' coverage of the genome, on and off the provided intervals."

  def getJointHistogramPath(dir: Path): Path = dir / "jh"
}
