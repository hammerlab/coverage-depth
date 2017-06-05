package org.hammerlab.pageant.coverage

import org.apache.spark.SparkContext
import org.hammerlab.commands.{ Args, SparkCommand }
import org.hammerlab.genomics.readsets.ReadSets
import org.hammerlab.genomics.readsets.args.impl.{ Arguments ⇒ ReadsetsArguments }
import org.hammerlab.genomics.readsets.args.path.{ UnprefixedPath, UnprefixedPathHandler, UnprefixedPathOptionHandler }
import org.hammerlab.genomics.reference.ContigName.Normalization.Lenient
import org.hammerlab.pageant.histogram.JointHistogram.{ fromFiles, load }
import org.hammerlab.paths.Path
import org.kohsuke.args4j.{ Option ⇒ Args4JOption }

class Arguments
  extends Args
    with ReadsetsArguments {

  @Args4JOption(
    name = "--out",
    required = true,
    usage = "Directory to write results to",
    handler = classOf[UnprefixedPathHandler],
    aliases = Array("-o"),
    metaVar = "DIR"
  )
  private var _outPath: UnprefixedPath = _
  def outPath: Path = _outPath.buildPath

  @Args4JOption(
    name = "--force",
    aliases = Array("-f"),
    usage = "Write result files even if they already exist"
  )
  var force: Boolean = false

  @Args4JOption(
    name = "--persist-joint-histogram",
    aliases = Array("-jh"),
    usage = "When set, save the computed joint-histogram; if one already exists, skip reading it, recompute it, and overwrite it"
  )
  var writeJointHistogram: Boolean = false

  @Args4JOption(
    name = "--intervals-file",
    aliases = Array("-i"),
    usage = "Intervals file or capture kit; print stats for loci matching this intervals file, not matching, and total.",
    handler = classOf[UnprefixedPathOptionHandler]
  )
  var _intervalsFileOpt: Option[UnprefixedPath] = None
  def intervalsFileOpt: Option[Path] = _intervalsFileOpt.map(_.buildPath)

  @Args4JOption(
    name = "--interval-partition-bytes",
    aliases = Array("-b"),
    usage = "Number of bytes per chunk of input interval-file"
  )
  var intervalPartitionBytes: Int = 1 << 16

  @Args4JOption(
    name = "--persist-distributions",
    aliases = Array("-v"),
    usage = "When set, persist full PDF and CDF of coverage-depth histogram"
  )
  var writeFullDistributions: Boolean = false
}

object CoverageDepth extends SparkCommand[Arguments] {

  override def defaultRegistrar: String = "org.hammerlab.pageant.kryo.Registrar"

  override def name: String = "coverage-depth"
  override def description: String = "Given one or two sets of reads, and an optional set of intervals, compute a joint histogram over the reads' coverage of the genome, on and off the provided intervals."

  override def run(args: Arguments, sc: SparkContext): Unit = {

    val (readsets, _) = ReadSets(sc, args)

    val contigLengths = readsets.contigLengths
    val totalReferenceLoci = contigLengths.totalLength

    val outPath = args.outPath

    val force = args.force
    val forceStr = if (force) " (forcing)" else ""

    val intervalsFileOpt = args.intervalsFileOpt

    val intervalsPathStr =
      intervalsFileOpt
        .map(intervalPath => s"against $intervalPath ")
        .getOrElse("")

    val jointHistogramPath = getJointHistogramPath(args.outPath)

    val jointHistogramPathExists = jointHistogramPath.exists

    val writeJointHistogram = args.writeJointHistogram

    val jh =
      if (!writeJointHistogram && jointHistogramPathExists) {
        println(s"Loading JointHistogram: $jointHistogramPath")
        load(sc, jointHistogramPath)
      } else {
        println(
          s"Analyzing ${args.paths.mkString("(", ", ", ")")} ${intervalsPathStr}and writing to $outPath$forceStr"
        )
        fromFiles(
          sc,
          args.paths,
          intervalsFileOpt.toList,
          bytesPerIntervalPartition = args.intervalPartitionBytes
        )
      }

    args.paths.length match {
      case 1 ⇒
        (if(intervalsFileOpt.isDefined)
          one_sample.with_intervals.ResultBuilder
        else
          one_sample.without_intervals.ResultBuilder
        )
        .make(jh, totalReferenceLoci)
        .save(
          outPath,
          force = force,
          writeFullDistributions = args.writeFullDistributions,
          writeJointHistogram = writeJointHistogram
        )
      case 2 ⇒
        (if (intervalsFileOpt.isDefined)
          two_sample.with_intervals.ResultBuilder
            else
          two_sample.without_intervals.ResultBuilder
        )
        .make(jh, totalReferenceLoci)
        .save(
          outPath,
          force = force,
          writeFullDistributions = args.writeFullDistributions,
          writeJointHistogram = writeJointHistogram
        )
      case num ⇒
        throw new IllegalArgumentException(
          s"Maximum of two reads-sets allowed; found $num: ${args.paths.mkString(",")}"
        )
    }
  }

  def getJointHistogramPath(dir: Path): Path = dir / "jh"
}
