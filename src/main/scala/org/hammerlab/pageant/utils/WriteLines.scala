package org.hammerlab.pageant.utils

import java.io.PrintWriter

import org.hammerlab.paths.Path

object WriteLines {
  def apply(dir: Path, fn: String, strs: Iterator[String], force: Boolean): Unit = {
    val path = dir / fn
    if (!force && path.exists) {
      println(s"Skipping $path, already exists")
    } else {
      val os = new PrintWriter(path.outputStream)
      strs.foreach(os.println)
      os.close()
    }
  }
}
