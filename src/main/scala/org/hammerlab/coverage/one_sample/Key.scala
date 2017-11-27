package org.hammerlab.coverage.one_sample

import cats.Monoid
import org.hammerlab.coverage
import org.hammerlab.coverage.histogram.JointHistogram.Depth

abstract class Key[C: Monoid]
  extends coverage.Key[C, Depth]
