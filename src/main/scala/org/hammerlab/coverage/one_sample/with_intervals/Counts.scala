package org.hammerlab.coverage.one_sample.with_intervals

import org.hammerlab.coverage.one_sample.Count
import org.hammerlab.coverage.utils.Monoid

case class Counts(on: Count, off: Count) {
  def +(o: Counts): Counts = Counts(on + o.on, off + o.off)
  @transient lazy val all: Count = on + off
}

object Counts {
  val empty = Counts(Count.empty, Count.empty)

  implicit val monoid =
    new Monoid[Counts] {
      override def id: Counts = empty
      override def op(x: Counts, y: Counts): Counts = x + y
    }
}

