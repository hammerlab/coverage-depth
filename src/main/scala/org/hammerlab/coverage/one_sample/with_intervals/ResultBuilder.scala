package org.hammerlab.coverage.one_sample.with_intervals

import org.hammerlab.coverage.histogram.JointHistogram
import org.hammerlab.coverage.histogram.JointHistogram._
import org.hammerlab.coverage.one_sample
import org.hammerlab.coverage.one_sample.{ CDF, Count, PDF }
import org.hammerlab.genomics.reference.NumLoci

object ResultBuilder
  extends one_sample.ResultBuilder[Key, Counts, Result] {

  type DC = (Depth, Counts)

  override def make(jh: JointHistogram,
                    pdf: PDF[Counts],
                    cdf: CDF[Counts],
                    filteredCDF: Array[(Depth, Counts)],
                    maxDepth: Depth,
                    firstCounts: Counts,
                    totalReferenceLoci: NumLoci): Result = {

    val Counts(Count(onBases, onLoci), Count(offBases, offLoci)) = firstCounts

    Result(
      jh,
      pdf,
      cdf,
      ReadSetStats(maxDepth, offBases, onBases),
      filteredCDF,
      onLoci,
      offLoci
    )
  }
}
