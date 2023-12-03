package virusdave

import slick.lifted.Query
import virusdave.db.SlickPgProfile.api._
import virusdave.db.SlickPgProfile.ElemWitness

object BetterInSet {

  implicit class _BetterInSetOps[A, R, C[_]](in: Query[A, R, C]) {
    def betterInSet(as: Set[A])(implicit ev: ElemWitness[A]): Query[A, R, C] = {
      in.inSet(as)
      Query(as.toList)
    }
  }
}