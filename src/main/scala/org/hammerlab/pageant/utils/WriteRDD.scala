package org.hammerlab.pageant.utils

import grizzled.slf4j.Logging
import org.apache.spark.rdd.RDD
import org.hammerlab.csv._
import org.hammerlab.paths.Path

import scala.reflect.runtime.universe.TypeTag

object WriteRDD
  extends Logging {
  def apply[T <: Product : TypeTag](path: Path, rdd: RDD[T], force: Boolean): Unit = {
    val csvLines = rdd.mapPartitions(_.toCSV(includeHeaderLine = false))
    (path.exists, force) match {
      case (true, true) ⇒
        logger.info(s"Removing $path")
        path.delete(recursive = true)
        csvLines.saveAsTextFile(path.toString)
      case (true, false) ⇒
        logger.info(s"Skipping $path, already exists")
      case _ ⇒
        csvLines.saveAsTextFile(path.toString)
    }
  }
}
