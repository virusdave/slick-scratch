package virusdave.db

import scala.util.Try
import slick.lifted.{Compilable, Compiled, CompilersMixin}
import slick.basic.BasicProfile

object Precompiled {
  /**
    * Create a new `Compiled` value for a raw value that is `Compilable`.
    * This will also precompute (not just lazily cache) the compiled version of all statements.
    */
  @inline def apply[V, C <: Compiled[V]](raw: V)(implicit compilable: Compilable[V, C], driver: BasicProfile): C = {
    val c = Compiled(raw)

    Try { c.asInstanceOf[CompilersMixin] } map { d =>
      Try(d.compiledQuery)
      Try(d.compiledDelete)
      Try(d.compiledInsert)
      Try(d.compiledUpdate)
    }

    c
  }


  //def cc[A <: Product, B, V <: A=>B, C <: Compiled[V]]
}