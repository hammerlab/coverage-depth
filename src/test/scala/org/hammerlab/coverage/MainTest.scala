package org.hammerlab.coverage

import hammerlab.path._
import org.hammerlab.cli.base.app.Arg
import org.hammerlab.cli.spark.MainSuite
import org.hammerlab.cmp.CanEq.Cmp
import org.hammerlab.cmp.Cmp
import org.hammerlab.test.matchers.files.DirMatcher
import org.hammerlab.test.resources.File

class MainTest
  extends MainSuite(Main) {

  def file(name: String): Path = File(name).path

  val r1 = file("r1.sam")
  val r2 = file("r2.sam")

  implicit val cmpPath: Cmp.Aux[Path, String] =
    new Cmp[Path] {
      type Diff = String
      def cmp(actual: Path, expected: Path): Option[String] = {
        val result = new DirMatcher(expected).apply(actual)
        if (result.matches)
          None
        else
          Some(
            result.rawFailureMessage
          )
      }
    }

  test("one sample with intervals") {
    check(
      "coverage.intervals.golden",
      "--intervals-file", file("intervals.bed"),
      "--paths", r1,
      "--sample-names", "r1"
    )
  }

  test("one sample with intervals depths-map") {
    check(
      "coverage.intervals.golden",
      "--intervals-file", file("intervals.bed2"),
      "--paths", r1,
      "--sample-names", "r1"
    )
  }

  test("one sample without intervals") {
    check(
      "coverage.golden",
      "--paths", r1,
      "--sample-names", "r1"
    )
  }

  test("two samples with intervals") {
    check(
      "coverage.intervals.golden2",
      "--intervals-file", file("intervals.bed"),
      "--paths", s"$r1,$r2",
      "--sample-names", "r1,r2"
    )
  }

  test("two samples without intervals") {
    check(
      "coverage.golden2",
      "--paths", s"$r1,$r2",
      "--sample-names", "r1,r2"
    )
  }

  def check(expectedDir: String, extraArgs: Arg*): Unit = {
    val outDir = tmpDir()
    appContainer.main(extraArgs ++ Seq[Arg]("-v", "-o", outDir): _*)
    ==(
      outDir,
      file(expectedDir)
    )
  }
}
