package org.hammerlab.coverage.one_sample.without_intervals

import org.hammerlab.coverage.histogram.JointHistogram.{ Depth, JointHistKey }
import org.hammerlab.coverage.{ IsKey, one_sample }
import org.hammerlab.coverage.one_sample.Count
import org.hammerlab.genomics.reference.NumLoci

case class Key(depth: Depth,
               numLoci: NumLoci)
  extends one_sample.Key[Count] {

  override def toCounts: Count =
    Count(
      depth * numLoci,
      numLoci
    )
}

object Key {
  implicit val isKey =
    new IsKey[Key] {
      override def make(kv: (JointHistKey, NumLoci)): Key = {
        val ((_, depths), count) = kv
        new Key(depths(0).get, count)
      }
    }
}
