package org.hammerlab.coverage.one_sample

import org.apache.spark.rdd.RDD
import org.hammerlab.coverage
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import spire.algebra.Monoid

class CDF[C: Monoid](val rdd: RDD[(Depth, C)])
  extends coverage.CDF[C]

object CDF {
  implicit def unwrapCDF[C: Monoid](cdf: CDF[C]): RDD[(Depth, C)] = cdf.rdd
}
