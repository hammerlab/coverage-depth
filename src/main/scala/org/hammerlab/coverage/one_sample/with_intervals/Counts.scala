package org.hammerlab.coverage.one_sample.with_intervals

import hammerlab.monoid._
import org.hammerlab.coverage.one_sample.Count

case class Counts(on: Count, off: Count) {
  @transient lazy val all: Count = on |+| off
}
