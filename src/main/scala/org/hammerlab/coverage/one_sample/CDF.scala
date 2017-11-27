package org.hammerlab.coverage.one_sample

import cats.Monoid
import org.apache.spark.rdd.RDD
import org.hammerlab.coverage
import org.hammerlab.coverage.histogram.JointHistogram.Depth

class CDF[C: Monoid](val rdd: RDD[(Depth, C)])
  extends coverage.CDF[C]

object CDF {
  implicit def unwrapCDF[C: Monoid](cdf: CDF[C]): RDD[(Depth, C)] = cdf.rdd
}
