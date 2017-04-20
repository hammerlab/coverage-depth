package org.hammerlab.pageant.utils

import java.io.PrintWriter

import grizzled.slf4j.Logging
import org.hammerlab.paths.Path

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
