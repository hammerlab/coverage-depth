package org.hammerlab.coverage.utils

import java.io.PrintWriter

import grizzled.slf4j.Logging
import hammerlab.path._

object WriteLines
  extends Logging {
  def apply(path: Path, strs: Iterator[String], force: Boolean): Unit =
    if (!force && path.exists) {
      logger.info(s"Skipping $path, already exists")
    } else {
      val os = new PrintWriter(path.outputStream)
      strs.foreach(os.println)
      os.close()
    }
}
