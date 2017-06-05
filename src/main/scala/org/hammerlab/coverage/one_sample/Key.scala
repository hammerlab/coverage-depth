package org.hammerlab.coverage.one_sample

import org.hammerlab.coverage
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import spire.algebra.Monoid

abstract class Key[C: Monoid]
  extends coverage.Key[C, Depth]
