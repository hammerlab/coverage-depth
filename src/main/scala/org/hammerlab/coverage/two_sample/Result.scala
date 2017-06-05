package org.hammerlab.coverage.two_sample

import java.io.PrintWriter
import java.nio.file.Files

import org.hammerlab.coverage.CoverageDepth.getJointHistogramPath
import org.hammerlab.coverage.histogram.JointHistogram
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import org.hammerlab.coverage.utils.{ WriteLines, WriteRDD }
import org.hammerlab.csv._
import org.hammerlab.paths.Path
import spire.algebra.Monoid

import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

abstract class Result[C: Monoid, CSVRow <: Product : TypeTag : ClassTag]
  extends Serializable {

  def jh: JointHistogram
  def pdf: PDF[C]
  def cdf: CDF[C]

  def toCSVRow(d2c: ((Depth, Depth), C)): CSVRow
  def writeMisc(pw: PrintWriter): Unit

  def save(dir: Path,
           force: Boolean = false,
           writeFullDistributions: Boolean = false,
           writeJointHistogram: Boolean = false): this.type = {

    if (!dir.exists) {
      Files.createDirectories(dir)
    }

    if (writeFullDistributions) {
      WriteRDD(dir / "pdf", pdf.rdd.map(toCSVRow), force)
      WriteRDD(dir / "cdf", cdf.rdd.map(toCSVRow), force)
    }

    if (writeJointHistogram) {
      val jhPath = getJointHistogramPath(dir)

      if (jhPath.exists) {
        jhPath.delete(recursive = true)
      }

      jh.write(jhPath)
    }

    WriteLines(dir / "pdf.csv", pdf.filtered.map(toCSVRow).toCSV(), force)
    WriteLines(dir / "cdf.csv", cdf.filtered.map(toCSVRow).toCSV(), force)

    val miscPath = dir / "misc"
    if (force || !miscPath.exists) {
      val pw = new PrintWriter(miscPath.outputStream)
      writeMisc(pw)
      pw.close()
    }

    this
  }
}
