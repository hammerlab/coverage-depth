name := "coverage-depth"
version := "1.0.0-SNAPSHOT"

addSparkDeps

deps ++= Seq(
  adam % "0.23.2-SNAPSHOT",
  args4j,
  args4s % "1.2.3",
  bdg_formats,
  bdg_utils_cli % "0.2.15",
  htsjdk,
  loci % "2.0.0-SNAPSHOT",
  magic_rdds % "1.5.0-SNAPSHOT",
  paths % "1.1.1-SNAPSHOT",
  readsets % "1.0.6-SNAPSHOT",
  spark_commands % "1.0.5-SNAPSHOT",
  spire,
  string_utils % "1.2.0-SNAPSHOT"
)

compileAndTestDeps += reference % "1.4.0-SNAPSHOT"

takeFirstLog4JProperties

excludeFilter in sbt.Test := NothingFilter
