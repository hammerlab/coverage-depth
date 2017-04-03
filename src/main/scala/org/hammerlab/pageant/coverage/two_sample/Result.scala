package org.hammerlab.pageant.coverage.two_sample

import java.io.PrintWriter

import org.hammerlab.csv._
import org.hammerlab.pageant.coverage.CoverageDepth.getJointHistogramPath
import org.hammerlab.pageant.histogram.JointHistogram
import org.hammerlab.pageant.histogram.JointHistogram.Depth
import org.hammerlab.pageant.utils.{ WriteLines, WriteRDD }
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

    if (writeFullDistributions) {
      WriteRDD(dir, s"pdf", pdf.rdd.map(toCSVRow), force)
      WriteRDD(dir, s"cdf", cdf.rdd.map(toCSVRow), force)
    }

    if (writeJointHistogram) {
      val jhPath = getJointHistogramPath(dir)

      if (jhPath.exists) {
        jhPath.delete(recursive = true)
      }

      jh.write(jhPath)
    }

    WriteLines(dir, s"pdf.csv", pdf.filtered.map(toCSVRow).toCSV(), force)
    WriteLines(dir, s"cdf.csv", cdf.filtered.map(toCSVRow).toCSV(), force)

    val miscPath = dir / "misc"
    if (force || !miscPath.exists) {
      val pw = new PrintWriter(miscPath.outputStream)
      writeMisc(pw)
      pw.close()
    }

    this
  }
}
