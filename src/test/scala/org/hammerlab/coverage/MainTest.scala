package org.hammerlab.coverage

import org.bdgenomics.utils.cli.Args4j
import org.hammerlab.test.matchers.files.DirMatcher.dirMatch
import org.hammerlab.test.resources.File

class MainTest
  extends Suite {

  test("one sample with intervals") {
    check(
      "coverage.intervals.golden",
      "--intervals-file", File("intervals.bed"),
      File("r1.sam")
    )
  }

  test("one sample with intervals depths-map") {
    check(
      "coverage.intervals.golden",
      "--intervals-file", File("intervals.bed2"),
      File("r1.sam")
    )
  }

  test("one sample without intervals") {
    check(
      "coverage.golden",
      File("r1.sam")
    )
  }

  test("two samples with intervals") {
    check(
      "coverage.intervals.golden2",
      "--intervals-file", File("intervals.bed"),
      File("r1.sam"),
      File("r2.sam")
    )
  }

  test("two samples without intervals") {
    check(
      "coverage.golden2",
      File("r1.sam"),
      File("r2.sam")
    )
  }

  def check(expectedDir: String, extraArgs: String*): Unit = {
    val outDir = tmpDir()
    val args =
      Args4j[Arguments](
        Array[String](
          "-v",
          "--out", outDir.toString
        ) ++
          extraArgs
      )

    Main.run(args, sc)

    outDir should dirMatch(expectedDir)
  }
}
