package org.hammerlab.coverage.two_sample.without_intervals

import org.hammerlab.coverage.NumBP
import org.hammerlab.coverage.histogram.JointHistogram.Depth

case class ReadSetStats(maxDepth: Depth, totalBases: NumBP)
