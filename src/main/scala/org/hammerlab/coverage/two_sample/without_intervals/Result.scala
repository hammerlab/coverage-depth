package org.hammerlab.coverage.two_sample.without_intervals

import java.io.PrintWriter

import hammerlab.monoid._
import org.hammerlab.coverage.histogram.JointHistogram
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import org.hammerlab.coverage.two_sample
import org.hammerlab.coverage.two_sample.Count
import org.hammerlab.genomics.reference.NumLoci

case class Result(jh: JointHistogram,
                  pdf: PDF,
                  cdf: CDF,
                  sample1Stats: ReadSetStats,
                  sample2Stats: ReadSetStats,
                  totalCoveredLoci: NumLoci,
                  totalReferenceLoci: NumLoci)
  extends two_sample.Result[Count, CSVRow] {

  @transient lazy val ReadSetStats(maxDepth1, totalBases1) = sample1Stats
  @transient lazy val ReadSetStats(maxDepth2, totalBases2) = sample2Stats

  override def toCSVRow(d2c: ((Depth, Depth), Count)): CSVRow =
    CSVRow(
      d2c,
      totalBases1,
      totalBases2,
      totalCoveredLoci,
      totalReferenceLoci
    )

  override def writeMisc(pw: PrintWriter): Unit = {
    pw.println(s"Max depths: $maxDepth1,$maxDepth2")
    pw.println(s"Total mapped bases: $totalBases1,$totalBases2")
    pw.println(s"Total covered loci: $totalCoveredLoci")
    pw.println(s"Total reference loci: $totalReferenceLoci")
  }
}
