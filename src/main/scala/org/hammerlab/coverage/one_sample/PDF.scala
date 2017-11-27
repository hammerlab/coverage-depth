package org.hammerlab.coverage.one_sample

import cats.Monoid
import magic_rdds.scan._
import org.apache.spark.rdd.RDD
import org.hammerlab.coverage
import org.hammerlab.coverage.histogram.JointHistogram.Depth

import scala.reflect.ClassTag

abstract class PDF[C: Monoid: ClassTag]
  extends coverage.PDF[C] {
  def rdd: RDD[(Depth, C)]
  def cdf: CDF[C] = new CDF(rdd.scanRightValuesInclusive)
}

object PDF {
  implicit def unwrapPDF[C: Monoid](pdf: PDF[C]): RDD[(Depth, C)] = pdf.rdd
}
