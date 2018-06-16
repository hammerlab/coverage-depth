name := "coverage-depth"
v"1.0.0"

spark

import genomics.{ loci, readsets, reference }
dep(
            adam % "0.23.4",
     bdg.formats,
       cli. base % "1.0.0" +testtest,
       cli.spark % "1.0.0" +testtest,
          htsjdk,
            loci % "2.2.0",
      magic_rdds % "4.2.3",
      math.utils % "2.2.0",
           paths % "1.5.0",
        readsets % "1.2.1",
       reference % "1.5.0" + testtest,
      spark_util % "3.0.0",
           spire,
    string_utils % "1.2.0",
           types % "1.2.0"
)

takeFirstLog4JProperties

// Tests expect some "hidden" files (basenames starting with ".") to be copied to the test classpath as test resources
excludeFilter in sbt.Test := NothingFilter
