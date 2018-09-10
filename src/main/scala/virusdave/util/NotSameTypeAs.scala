package virusdave.util

import scala.annotation.implicitAmbiguous

// Utilities related to better use of the scala type system.  Note that it's entirely possible to "fool" the compiler
// by making valid type casts to different types, or by manually providing `null` for the evidence parameter (which
// would be "safe" in the sense that the typeclasses are empty, but would circumvent the clearly intended type
// constraint).
//
// These should probably move out of the `db` project to a yet-more-root `base` project at some point, since they're
// not at all db-related.  However, `db` is currently our root project, so...

/**
  * Helper to do typelevel encoding of "A is not the same type as B".
  *
  * def foo[A, B](a: A, b: B)(implicit ev: A NotSameTypeas B) = ...
  */
sealed trait NotSameTypeAs[A, B]
object NotSameTypeAs {
  // Uses ambiguity of implicits to rule out the cases we're trying to exclude
  implicit def neq[A, B]: A NotSameTypeAs B = null

  @implicitAmbiguous("Couldn't prove that ${A} is NOT the same type as ${B}")
  implicit def neqAmbig1[A, B](implicit ev: A =:= B): A NotSameTypeAs B = null
  implicit def neqAmbig2[A, B](implicit ev: A =:= B): A NotSameTypeAs B = null
}