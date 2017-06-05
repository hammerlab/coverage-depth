package org.hammerlab.coverage

import spire.algebra.Monoid

abstract class Key[C: Monoid, DepthsT] {
  def depth: DepthsT
  def toCounts: C
}
