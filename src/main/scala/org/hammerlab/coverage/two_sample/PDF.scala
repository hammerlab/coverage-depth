package org.hammerlab.coverage.two_sample

import cats.Monoid
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.hammerlab.coverage
import org.hammerlab.coverage.histogram.JointHistogram.Depth

import scala.collection.immutable.SortedSet

case class PDF[C: Monoid](rdd: RDD[((Depth, Depth), C)],
                          filtersBroadcast: Broadcast[(SortedSet[Depth], SortedSet[Depth])],
                          maxDepth1: Depth,
                          maxDepth2: Depth)
  extends coverage.PDF[C]
    with CanDownSampleRDD[C]

case class CDF[C: Monoid](rdd: RDD[((Depth, Depth), C)],
                          filtersBroadcast: Broadcast[(SortedSet[Depth], SortedSet[Depth])])
  extends coverage.CDF[C]
    with CanDownSampleRDD[C]
