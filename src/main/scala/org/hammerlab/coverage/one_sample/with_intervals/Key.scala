package org.hammerlab.coverage.one_sample.with_intervals

import hammerlab.monoid._
import org.hammerlab.coverage.histogram.JointHistogram.{ Depth, JointHistKey }
import org.hammerlab.coverage.{ IsKey, one_sample }
import org.hammerlab.coverage.one_sample.Count
import org.hammerlab.genomics.reference.NumLoci

case class Key(depth: Depth,
               numLociOn: NumLoci,
               numLociOff: NumLoci)
  extends one_sample.Key[Counts] {

  override def toCounts: Counts =
    Counts(
      Count(numLociOn * depth, numLociOn),
      Count(numLociOff * depth, numLociOff)
    )
}

object Key {
  implicit val isKey =
    new IsKey[Key] {
      override def make(kv: (JointHistKey, NumLoci)): Key = {
        val ((_, depths), numLoci) = kv

        val (on, off) =
          if (depths(1).get == 1)
            (numLoci, NumLoci(0))
          else
            (NumLoci(0), numLoci)

        new Key(depths(0).get, on, off)
      }
    }
}

