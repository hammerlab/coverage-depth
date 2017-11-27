package org.hammerlab.coverage.two_sample

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.hammerlab.coverage.histogram.JointHistogram.Depth

import scala.collection.immutable.SortedSet

trait CanDownSampleRDD[V] {
  def rdd: RDD[((Depth, Depth), V)]
  def filtersBroadcast: Broadcast[(SortedSet[Depth], SortedSet[Depth])]
  @transient lazy val filtered = filterDistribution(filtersBroadcast)

  private def filterDistribution(filtersBroadcast: Broadcast[(SortedSet[Int], SortedSet[Int])]): Array[((Depth, Depth), V)] =
    (for {
      ((d1, d2), value) ← rdd
      (d1Filter, d2Filter) = filtersBroadcast.value
      if d1Filter(d1) && d2Filter(d2)
    } yield
      (d1, d2) → value
    )
    .collect
    .sortBy(_._1)
}
