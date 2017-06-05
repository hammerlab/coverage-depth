package org.hammerlab.coverage.one_sample.without_intervals

import java.io.PrintWriter

import org.hammerlab.coverage.histogram.JointHistogram
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import org.hammerlab.coverage.{ NumBP, one_sample }
import org.hammerlab.coverage.one_sample.without_intervals.ResultBuilder.DC
import org.hammerlab.coverage.one_sample.{ CDF, Count, PDF }
import org.hammerlab.genomics.reference.NumLoci

/**
 * Statistics about one set of reads' coverage of a set of intervals.
 *
 * @param jh               joint-histogram of read coverage vs. interval coverage (the latter being 1 or 0 everywhere).
 * @param pdf              ([[Depth]], [[Count]]) tuples indicating on-target and off-target coverage at all observed depths.
 * @param cdf              CDF of the PDF above; tuples represent numbers of on- and off-target loci with depth *at least* a given
 *                         number.
 * @param maxDepth         maximum locus-coverage depth
 * @param totalBases       total number of sequenced bases
 * @param filteredCDF      summary CDF, filtered to a few logarithmically-spaced round-numbers.
 * @param totalCoveredLoci total number of loci.
 */
case class Result(jh: JointHistogram,
                  pdf: PDF[Count],
                  cdf: CDF[Count],
                  maxDepth: Depth,
                  totalBases: NumBP,
                  filteredCDF: Array[DC],
                  totalCoveredLoci: NumLoci,
                  totalReferenceLoci: NumLoci)
  extends one_sample.Result[Count, CSVRow] {

  override def toCSVRow(depthCounts: DC): CSVRow = CSVRow(depthCounts, totalBases, totalCoveredLoci, totalReferenceLoci)

  override def writeMisc(pw: PrintWriter): Unit = {
    pw.println(s"Max depth: $maxDepth")
    pw.println(s"Total mapped bases: $totalBases")
    pw.println(s"Total covered loci: $totalCoveredLoci")
    pw.println(s"Total reference loci: $totalReferenceLoci")
  }
}
