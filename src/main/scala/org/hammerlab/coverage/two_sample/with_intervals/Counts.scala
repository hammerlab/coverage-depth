package org.hammerlab.coverage.two_sample.with_intervals

import hammerlab.monoid._
import org.hammerlab.coverage.two_sample.Count

case class Counts(on: Count, off: Count) {
  @transient lazy val all: Count = on |+| off
}
