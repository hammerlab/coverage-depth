package org.hammerlab.coverage

import spire.algebra.Monoid

abstract class PDF[C: Monoid] extends Serializable

class CDF[T] extends Serializable
