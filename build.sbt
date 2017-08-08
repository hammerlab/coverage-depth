name := "coverage-depth"
version := "1.0.0-SNAPSHOT"

addSparkDeps

deps ++= Seq(
  adam % "0.23.2",
  args4j,
  args4s % "1.3.0",
  bdg_formats,
  bdg_utils_cli % "0.3.0",
  htsjdk,
  loci % "2.0.1",
  magic_rdds % "1.5.0-SNAPSHOT",
  math % "1.0.0",
  paths % "1.2.0",
  readsets % "1.0.6-SNAPSHOT",
  spark_commands % "1.1.0-SNAPSHOT",
  spire,
  string_utils % "1.2.0"
)

compileAndTestDeps += reference % "1.4.0"

takeFirstLog4JProperties

excludeFilter in sbt.Test := NothingFilter
