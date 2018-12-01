package virusdave

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.higherKinds
import scala.reflect.ClassTag
import shapeless.{Generic, HList, Lazy, the}
import shapeless.ops.hlist.Tupler
import slick.lifted.{CaseClassShape => _, ColumnsShapeLevel => _, MappedScalaProductShape => _, Query => _, Rep => _, Shape => _, ShapeLevel => _, _}
import slick.util.TupleSupport
import slickless._
import virusdave.db.gen.Tables._
import virusdave.db.{Precompiled, SlickPgProfile}
import virusdave.db.SlickPgProfile.api._
import virusdave.util.NotSameTypeAs

object Polymorphic {
  type ProductCaseClassShape[LiftedTuple, LiftedCaseClass <: Product, PlainTuple, PlainCaseClass <: Product] =
    CaseClassShape[Product, LiftedTuple, LiftedCaseClass, PlainTuple, PlainCaseClass]

//  abstract class PolymorphicProduct[ME[_ <: Id[_] with Rep[_]] <: PolymorphicProduct[ME]] {
//    implicit def baseShape: Shape[ColumnsShapeLevel, ME[Id], Me[Id], _]
//    implicit def fullShape: Shape[]
//  }
//  case class FIntBool[F[_] <: Id[_] with Rep[_]](i: F[Int], b: F[Boolean]) extends PolymorphicProduct[FIntBool]
  case class FIntBool[F[_]](i: F[Int], b: F[Boolean])
}

object ShapelessPolymorphic {
  // Provide a generic Shape between a lifted and non-lifted case class:
  // Given a `Shape[Level, A, B, A]` and two Generics CCA<~>A and CCB<~>B, produce a Shape[Level, CCA, CCB, CCA]
  implicit def _liftTupleShapeToCaseClasses[Level <: ShapeLevel, CCA <: Product, CCB <: Product, A <: HList, B <: HList](
      implicit
      genericA: /*Lazy[*/Generic.Aux[CCA, A]/*]*/,
      genericB: /*Lazy[*/Generic.Aux[CCB, B]/*]*/,
      shape: /*Lazy[*/HListShape[Level, A, B, A]/*]*/,/*
      genericA: Lazy[Generic.Aux[CCA, A]],
      genericB: Lazy[Generic.Aux[CCB, B]],*/
      classTagB: ClassTag[B],
      classTagCCB: ClassTag[CCB])
      : Shape[Level, CCA, CCB, CCA] = new ProductNodeShape[Level, Product, CCA, CCB, CCA] {
    override val shapes: Seq[Shape[_ <: ShapeLevel, _, _, _]] = shape./*value.*/shapes
    override def buildValue(elems: IndexedSeq[Any]): Any = genericA./*value.*/from(shape./*value.*/buildValue(elems).asInstanceOf[A])
    override def copy(shapes: Seq[Shape[_ <: ShapeLevel, _, _, _]])/*: Shape[Level, _, _, _]*/ =
      // TODO(dave): FIXME
      _liftTupleShapeToCaseClasses(genericA, genericB, shape/*new HListShape(shapes)*/, /*genericA.value, genericB.value, */classTagB, classTagCCB) // Intellij lies!
    override def getElement(value: Product, idx: Int): Any = value.productElement(idx)
  }

  implicit def _lowerTupleShapeToCaseClass[Level <: ShapeLevel, CCA <: Product, CCB <: Product, A <: HList, B <: HList](
      implicit
      genericA: Generic.Aux[CCA, A],
      genericB: Generic.Aux[CCB, B],
      shape: HListShape[Level, A, B, A],
      shapeLowered: HListShape[Level, B, B, _],
      evANotB: CCA NotSameTypeAs CCB)
      : Shape[Level, CCB, CCB, _] = new ProductNodeShape[Level, Product, CCB, CCB, CCA] {
    override val shapes: Seq[Shape[_ <: ShapeLevel, _, _, _]] = shapeLowered.shapes
    override def buildValue(elems: IndexedSeq[Any]): Any = genericB.from(shapeLowered.buildValue(elems).asInstanceOf[B])
    override def copy(shapes: Seq[Shape[_ <: ShapeLevel, _, _, _]])/*: Shape[Level, _, _, _]*/ =
      // TODO(dave): FIXME
      //_lowerTupleShapeToCaseClass(genericB, shapeLowered /*new HListShape(shapes)*/, shape, genericA, evANotB)  // Intellij lies!
      ???
    override def getElement(value: Product, idx: Int): Any = value.productElement(idx)
  }
}

object Monomorphic {
  class CCShape[LiftedTuple, LiftedCaseClass <: Product, PlainTuple, PlainCaseClass <: Product](
      mapLifted: LiftedTuple => LiftedCaseClass,
      mapPlain: PlainTuple => PlainCaseClass)(
      implicit columnShapes: Shape[ColumnsShapeLevel, LiftedTuple, PlainTuple, LiftedTuple],
      unpackedShapes: Shape[ColumnsShapeLevel, PlainTuple, PlainTuple, /*LiftedTuple*/ _],
      classTag: ClassTag[PlainCaseClass])
      extends MappedScalaProductShape[ColumnsShapeLevel, Product, LiftedCaseClass, PlainCaseClass, LiftedCaseClass] {
    val shapes = columnShapes.asInstanceOf[TupleShape[_,_,_,_]].shapes
    override def toMapped(v: Any) = mapPlain(v.asInstanceOf[PlainTuple])
    def buildValue(elems: IndexedSeq[Any]) = mapLifted(TupleSupport.buildTuple(elems).asInstanceOf[LiftedTuple])
    def copy(s: Seq[Shape[_ <: ShapeLevel, _, _, _]]) = new CCShape(mapLifted, mapPlain) { override val shapes = s }
  }
}

object ScratchMain {
  //----------------------------------------------------------------------------------------------------
  // "Standard" precompilation
  //----------------------------------------------------------------------------------------------------
  private def findByPkey(pk: Rep[Int]) = {
    ScratchTestTable.filter(_.somePkey === pk)
  }
  val pcPkey = Precompiled(findByPkey _)

  //----------------------------------------------------------------------------------------------------

  //----------------------------------------------------------------------------------------------------
  // Monomorphic case class shape
  //----------------------------------------------------------------------------------------------------
  private def findByIntBoolRaw(i: Rep[Int], b: Rep[Boolean]) = {
    ScratchTestTable.filter { row =>
      row.someInteger === i && row.someBoolean === b
    }
  }
  val pcIntBoolRaw = Precompiled(findByIntBoolRaw _)

//  private def findByIntBoolIntRaw(i: Rep[Int], bi: (Rep[Boolean], Rep[Int])) = {
//    ScratchTestTable.filter { row =>
//      row.somePkey === i && row.someBoolean === bi._1 && row.someInteger === bi._2
//    }
//  }
//  val pcIntBoolIntRaw = Precompiled(findByIntBoolIntRaw _)
//
//  case class IntBool(i: Int, b: Boolean)
//  case class IntBoolLift(i: Rep[Int], b: Rep[Boolean])
//  implicit object IntBoolShape extends CaseClassShape(IntBoolLift.tupled, IntBool.tupled)
//
//  private def findByIntBool(ib: IntBoolLift) = findByIntBoolRaw(ib.i, ib.b)
//  // Below won't compile, missing shapes
//  //val bcIntBool = Precompiled(findByIntBool _)
  //----------------------------------------------------------------------------------------------------

  //----------------------------------------------------------------------------------------------------
  // Polymorphic case class shape
  //----------------------------------------------------------------------------------------------------
//  import Polymorphic._
//  type Id[A] = A
//  private val __1 = FIntBool[Id](42, false)
//  implicit object FIntBoolShape extends ProductCaseClassShape((FIntBool.apply[Rep] _).tupled, (FIntBool.apply[Id] _).tupled)

  // This works, since without the explicit type parameters, the compiler actually attempts to infer a `Product` type
  // as shown for `FIntBoolShape3` below.
//    implicit object FIntBoolShape2 extends CaseClassShape[
//      Product,
//      (Rep[Int], Rep[Boolean]),
//      FIntBool[Rep],
//      (Int, Boolean),
//      FIntBool[Id]
//      ]((FIntBool.apply[Rep] _).tupled, (FIntBool.apply[Id] _).tupled)

//    implicit object FIntBoolShape3 extends CaseClassShape[
//      FIntBool[_ >: Id with Rep],
//      (Rep[Int], Rep[Boolean]),
//      FIntBool[Rep],
//      (Int, Boolean),
//      FIntBool[Id]
//      ]((FIntBool.apply[Rep] _).tupled, (FIntBool.apply[Id] _).tupled)

//  private def findByPkeyPoly(pkey: Rep[Int]) = {
//    ScratchTestTable
//      .filter(_.somePkey === pkey)
//      .map { row => FIntBool(row.somePkey, row.someBoolean) }
//  }
//  val pcByPkeyPoly = Precompiled(findByPkeyPoly _)
//  val __2: Query[FIntBool[Rep], FIntBool[Id], Seq] = findByPkeyPoly(123)
//  val __3: DBIO[Seq[Id[Boolean]]] = pcByPkeyPoly(123).result.map(_.map(x => x.b))
//  private def findByFIntBool(ib: FIntBool[Rep]) = findByIntBoolRaw(ib.i, ib.b)
//  // Below won't compile, missing shapes
//  //val pcByFIntBool = Precompiled(findByFIntBool _)

  //----------------------------------------------------------------------------------------------------
  // Monomorphic case class ColumnsShapeLevel shape
  //----------------------------------------------------------------------------------------------------
//  import Monomorphic._
//  case class IntBool2(i: Int, b: Boolean)
//  case class IntBool2Lift(i: Rep[Int], b: Rep[Boolean])
//  implicit object IntBool2Shape extends CCShape(IntBool2Lift.tupled, IntBool2.tupled)
//  private def findByIntBool2(ib: IntBool2Lift) = findByIntBoolRaw(ib.i, ib.b)
//  // TODO(dave): FIXME
//  implicit def __4: Shape[ColumnsShapeLevel, IntBool2, IntBool2, _] = null
//  val bcIntBool2 = Precompiled(findByIntBool2 _)
  //----------------------------------------------------------------------------------------------------

  //----------------------------------------------------------------------------------------------------
  // Shapeless Polymorphic
  //----------------------------------------------------------------------------------------------------
  import ShapelessPolymorphic._
  case class IntBool3(i: Int, b: Boolean)
  case class IntBool3Lift(i: Rep[Int], b: Rep[Boolean])
  //implicit object IntBool3Shape extends CCShape(IntBool3Lift.tupled, IntBool3.tupled)
  private def findByIntBool3(ib: IntBool3Lift) = findByIntBoolRaw(ib.i, ib.b)
  private implicit val _gIB3 = Generic[IntBool3]
  private implicit val _gIB3L = Generic[IntBool3Lift]
//  private val __5 = implicitly[Shape[ColumnsShapeLevel, IntBool3Lift, IntBool3, IntBool3Lift]]
//  private val __6 = implicitly[Shape[ColumnsShapeLevel, IntBool3, IntBool3, _]]
//  val bcIntBool3 = Precompiled(findByIntBool3 _)

//  private implicit val __7 = implicitly[HListShape[ColumnsShapeLevel, _gIB3L.Repr, _gIB3.Repr, _gIB3L.Repr]]
  private implicit val __8 = implicitly[HListShape[ColumnsShapeLevel, _gIB3.Repr, _gIB3.Repr, _]]
//  private implicit val __9 = _liftTupleShapeToCaseClasses[ColumnsShapeLevel, IntBool3Lift, IntBool3, _gIB3L.Repr, _gIB3.Repr]
//  private implicit val _10 = implicitly[Shape[ColumnsShapeLevel, IntBool3Lift, IntBool3, IntBool3Lift]]
  //private implicit val _11 = implicitly[Shape[ColumnsShapeLevel, IntBool3, IntBool3, _]]

  // TODO(dave): Why does this next line fail to compile?
//  private implicit val _11 = implicitly[Shape[ColumnsShapeLevel, IntBool3, IntBool3, IntBool3Lift]]
  //----------------------------------------------------------------------------------------------------
  def main(args: Array[String]): Unit = {
    println("Hello, world!")
    val x1: String = "abc"
    val x2: Null = null
    println(x2 == x1)
//    println(s"${pcByPkeyPoly(123).result.statements}")
//    println(s"${bcIntBool2(IntBool2(123, true)).result.statements}")
  }
}
