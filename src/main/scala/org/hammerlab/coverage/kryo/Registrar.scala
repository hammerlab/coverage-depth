package org.hammerlab.coverage.kryo

import com.esotericsoftware.kryo.Kryo
import org.apache.spark.serializer.KryoRegistrator
import org.bdgenomics.adam.serialization.ADAMKryoRegistrator
import org.hammerlab.coverage
import org.hammerlab.coverage.histogram.{ JointHistogram, Record }
import org.hammerlab.coverage.one_sample
import org.hammerlab.coverage.one_sample.with_intervals.Counts
import org.hammerlab.coverage.two_sample.with_intervals
import org.hammerlab.genomics.reference.PermissiveRegistrar
import org.hammerlab.magic.rdd.grid.PartialSumGridRDD

import scala.collection.mutable

class Registrar extends KryoRegistrator {
  override def registerClasses(kryo: Kryo): Unit = {

    kryo.register(classOf[Record])

    kryo.register(classOf[Counts])
    kryo.register(classOf[Array[Counts]])
    kryo.register(classOf[one_sample.Count])

    kryo.register(classOf[with_intervals.Counts])
    kryo.register(classOf[coverage.two_sample.Count])

    kryo.register(classOf[Vector[_]])
    kryo.register(classOf[Array[Vector[_]]])
    kryo.register(classOf[mutable.WrappedArray.ofLong])
    kryo.register(classOf[mutable.WrappedArray.ofByte])
    kryo.register(classOf[mutable.WrappedArray.ofChar])
    kryo.register(classOf[Array[Char]])

    // Tuple2[Long, Any], afaict?
    // "J" == Long (obviously). https://github.com/twitter/chill/blob/6d03f6976f33f6e2e16b8e254fead1625720c281/chill-scala/src/main/scala/com/twitter/chill/TupleSerializers.scala#L861
    kryo.register(Class.forName("scala.Tuple2$mcJZ$sp"))
    kryo.register(Class.forName("scala.Tuple2$mcIZ$sp"))

    new ADAMKryoRegistrator().registerClasses(kryo)

    PartialSumGridRDD.register(kryo)
    JointHistogram.register(kryo)

    kryo.register(classOf[Array[String]])
    kryo.register(classOf[Array[Int]])

    // This seems to be necessary when serializing a RangePartitioner, which writes out a ClassTag:
    //
    //  https://github.com/apache/spark/blob/v1.6.1/core/src/main/scala/org/apache/spark/Partitioner.scala#L220
    //
    // See also:
    //
    //   https://mail-archives.apache.org/mod_mbox/spark-user/201504.mbox/%3CCAC95X6JgXQ3neXF6otj6a+F_MwJ9jbj9P-Ssw3Oqkf518_eT1w@mail.gmail.com%3E
    kryo.register(Class.forName("scala.reflect.ClassTag$$anon$1"))
    kryo.register(classOf[java.lang.Class[_]])

    kryo.register(classOf[mutable.WrappedArray.ofRef[_]])
    kryo.register(classOf[Array[Array[Byte]]])

    kryo.register(classOf[Array[Object]])

    PermissiveRegistrar.registerClasses(kryo)
  }
}
