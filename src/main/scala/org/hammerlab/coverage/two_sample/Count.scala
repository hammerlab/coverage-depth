package org.hammerlab.coverage.two_sample

import org.hammerlab.coverage.NumBP
import org.hammerlab.genomics.reference.NumLoci

case class Count(bp1: NumBP,
                 bp2: NumBP,
                 n: NumLoci) {
//  def +(o: Count): Count = Count(bp1 + o.bp1, bp2 + o.bp2, n + o.n)
}

//object Count {
//  val empty = Count(0, 0, NumLoci(0))
//
//  implicit val monoid =
//    new Monoid[Count] {
//      override def id: Count = empty
//      override def op(x: Count, y: Count): Count = x + y
//    }
//}
//
