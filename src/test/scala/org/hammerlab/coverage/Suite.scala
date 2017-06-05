package org.hammerlab.coverage

import org.hammerlab.coverage.kryo.Registrar
import org.hammerlab.spark.test.suite.KryoSparkSuite

abstract class Suite
  extends KryoSparkSuite(classOf[Registrar])

