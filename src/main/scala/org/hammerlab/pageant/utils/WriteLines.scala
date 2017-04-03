package org.hammerlab.pageant.utils

import java.io.PrintWriter

import org.hammerlab.paths.Path

object WriteLines {
  def apply(path: Path, strs: Iterator[String], force: Boolean): Unit =
    if (!force && path.exists) {
      println(s"Skipping $path, already exists")
    } else {
      val os = new PrintWriter(path.outputStream)
      strs.foreach(os.println)
      os.close()
    }
}
