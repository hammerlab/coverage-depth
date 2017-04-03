import sbtassembly.PathList

name := "pageant"
version := "1.0.0-SNAPSHOT"

sparkVersion := "2.1.0"

scala211Only
addSparkDeps

deps ++= Seq(
  libs.value('adam_core),
  libs.value('args4j),
  libs.value('args4s),
  libs.value('bdg_formats),
  libs.value('bdg_utils_cli),
  "com.google.cloud" % "google-cloud-nio" % "0.10.0-alpha",
  libs.value('htsjdk),
  libs.value('loci),
  libs.value('magic_rdds),
  libs.value('paths),
  libs.value('readsets),
  libs.value('spark_commands),
  libs.value('spire),
  libs.value('string_utils)
)

compileAndTestDeps += libs.value('reference)

takeFirstLog4JProperties

excludeFilter in Test := NothingFilter

publishAssemblyJar

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
  case x => (assemblyMergeStrategy in assembly).value(x)
}

shadeRenames ++= Seq(
  // google-cloud-nio uses Guava 19.0 and at least one API (Splitter.splitToList) that succeeds Spark's Guava (14.0.1).
  "com.google.common.**" -> "org.hammerlab.guava.common.@1",

  // GCS Connector uses an older google-api-services-storage than google-cloud-nio and breaks us without shading it in
  // here; see http://stackoverflow.com/a/39521403/544236.
  "com.google.api.services.**" -> "hammerlab.google.api.services.@1"
)
