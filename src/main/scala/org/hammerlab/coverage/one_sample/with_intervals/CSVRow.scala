package org.hammerlab.coverage.one_sample.with_intervals

import org.hammerlab.coverage.NumBP
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import org.hammerlab.coverage.one_sample.with_intervals.ResultBuilder.DC
import org.hammerlab.genomics.reference.NumLoci

case class CSVRow(depth: Depth,
                  onBP: NumBP,
                  numOnLoci: NumLoci,
                  fracBPOn: Double,
                  fracLociOn: Double,
                  offBP: NumBP,
                  numOffLoci: NumLoci,
                  fracBPOff: Double,
                  fracLociOff: Double)

object CSVRow {
  def apply(depthCounts: DC,
            totalBases: NumBP,
            totalOnLoci: NumLoci,
            totalOffLoci: NumLoci): CSVRow = {
    val (depth, Counts(on, off)) = depthCounts
    CSVRow(
      depth,
      on.bp,
      on.n,
      on.bp * 1.0 / totalBases,
      on.n * 1.0 / totalOnLoci,
      off.bp,
      off.n,
      off.bp * 1.0 / totalBases,
      off.n * 1.0 / totalOffLoci
    )
  }
}
