package org.hammerlab.coverage.kryo

import org.bdgenomics.adam.serialization.ADAMKryoRegistrator
import org.hammerlab.coverage.histogram.{ JointHistogram, Record }
import org.hammerlab.coverage.one_sample
import org.hammerlab.coverage.one_sample.with_intervals.Counts
import org.hammerlab.coverage.two_sample.with_intervals
import org.hammerlab.genomics.reference.{ PermissiveRegistrar, Position }
import org.hammerlab.kryo._
import org.hammerlab.magic.rdd.grid.{ BottomLeftElem, BottomRow, LeftCol }
import org.hammerlab.{ bam, coverage }

import scala.collection.immutable.TreeSet
import scala.collection.mutable

case class Registrar() extends spark.Registrar(

    cls[Record],

    arr[Counts],
    cls[one_sample.Count],

    cls[with_intervals.Counts],
    cls[coverage.two_sample.Count],

    arr[Vector[_]],

    cls[mutable.WrappedArray.ofLong],
    cls[mutable.WrappedArray.ofByte],
    cls[mutable.WrappedArray.ofChar],
    cls[Array[Char]],

    // Tuple2[Long, Any], afaict?
    // "J" == Long (obviously). https://github.com/twitter/chill/blob/6d03f6976f33f6e2e16b8e254fead1625720c281/chill-scala/src/main/scala/com/twitter/chill/TupleSerializers.scala#L861
    "scala.Tuple2$mcJZ$sp",
    "scala.Tuple2$mcIZ$sp",

    new ADAMKryoRegistrator(),

    //PartialSumGridRDD.register(kryo)
    cls[BottomLeftElem[_]],
    cls[BottomRow[_]],
    cls[LeftCol[_]],

    JointHistogram,

    cls[Array[String]],
    cls[Array[Int]],

    // This seems to be necessary when serializing a RangePartitioner, which writes out a ClassTag:
    //
    //  https://github.com/apache/spark/blob/v1.6.1/core/src/main/scala/org/apache/spark/Partitioner.scala#L220
    //
    // See also:
    //
    //   https://mail-archives.apache.org/mod_mbox/spark-user/201504.mbox/%3CCAC95X6JgXQ3neXF6otj6a+F_MwJ9jbj9P-Ssw3Oqkf518_eT1w@mail.gmail.com%3E
    "scala.reflect.ClassTag$$anon$1",
    cls[java.lang.Class[_]],

    cls[mutable.WrappedArray.ofRef[_]],
    cls[Array[Array[Byte]]],

    cls[Array[Object]],

    PermissiveRegistrar,

    /**
     * [[scala.collection.immutable.SortedSet]]s backed by [[TreeSet]]s get broadcasted in e.g.
     * [[org.hammerlab.coverage.one_sample.ResultBuilder.make]]
     */
    cls[TreeSet[_]],
    Ordering.Int.getClass,
    "scala.collection.immutable.RedBlackTree$BlackTree",
    "scala.collection.immutable.RedBlackTree$RedTree",

    bam.spark.load.Registrar(),

    arr[Position]
)
