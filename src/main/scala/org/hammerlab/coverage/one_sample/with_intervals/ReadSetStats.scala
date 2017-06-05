package org.hammerlab.coverage.one_sample.with_intervals

import org.hammerlab.coverage.NumBP
import org.hammerlab.coverage.histogram.JointHistogram.Depth

case class ReadSetStats(maxDepth: Depth,
                        offBases: NumBP,
                        onBases: NumBP) {
  lazy val totalBases = offBases + onBases
}
