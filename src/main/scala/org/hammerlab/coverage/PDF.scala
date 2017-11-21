package org.hammerlab.coverage

import cats.Monoid

abstract class PDF[C: Monoid] extends Serializable

class CDF[T] extends Serializable
