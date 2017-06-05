package org.hammerlab.coverage

import org.hammerlab.coverage.histogram.JointHistogram.JointHistKey
import org.hammerlab.genomics.reference.NumLoci

abstract class IsKey[K <: Key[_, _]]
  extends Serializable {
  def make(kv: (JointHistKey, NumLoci)): K
}
