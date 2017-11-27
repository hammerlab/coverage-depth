package org.hammerlab.coverage.one_sample.without_intervals

import hammerlab.monoid._
import org.hammerlab.coverage.histogram.JointHistogram
import org.hammerlab.coverage.histogram.JointHistogram.Depth
import org.hammerlab.coverage.one_sample
import org.hammerlab.coverage.one_sample.{ CDF, Count, PDF }
import org.hammerlab.genomics.reference.NumLoci

object ResultBuilder
  extends one_sample.ResultBuilder[Key, Count, Result] {

  type DC = (Depth, Count)

  override def make(jh: JointHistogram,
                    pdf: PDF[Count],
                    cdf: CDF[Count],
                    filteredCDF: Array[(Depth, Count)],
                    maxDepth: Depth,
                    firstCounts: Count,
                    totalReferenceLoci: NumLoci): Result =
    Result(
      jh,
      pdf,
      cdf,
      maxDepth,
      firstCounts.bp,
      filteredCDF,
      firstCounts.n,
      totalReferenceLoci
    )
}
