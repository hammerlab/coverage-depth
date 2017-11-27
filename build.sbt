name := "coverage-depth"
version := "1.0.0-SNAPSHOT"

addSparkDeps

deps ++= Seq(
  adam % "0.23.2",
  args4j,
  args4j_cli % "1.2.0",
  args4s % "1.3.0",
  bdg_formats,
  bdg_utils_cli % "0.3.0",
  htsjdk,
  loci % "2.0.1",
  magic_rdds % "4.0.0",
  math % "2.1.0",
  paths % "1.4.0",
  readsets % "1.1.0",
  reference % "1.4.0" + testtest,
  spark_util % "2.0.1",
  spire,
  string_utils % "1.2.0",
  types % "1.0.1"
)

takeFirstLog4JProperties

// Tests expect some "hidden" files (basenames starting with ".") to be copied to the test classpath as test resources
excludeFilter in sbt.Test := NothingFilter
