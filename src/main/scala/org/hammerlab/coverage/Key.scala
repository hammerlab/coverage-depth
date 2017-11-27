package org.hammerlab.coverage

import cats.Monoid

abstract class Key[C: Monoid, DepthsT] {
  def depth: DepthsT
  def toCounts: C
}
