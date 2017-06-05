package org.hammerlab.coverage.two_sample

import org.hammerlab.coverage
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import spire.algebra.Monoid

abstract class Key[C: Monoid]
  extends coverage.Key[C, (Depth, Depth)] {
  def depth1: Depth
  def depth2: Depth
  def depth: (Depth, Depth) = (depth1, depth2)
}
