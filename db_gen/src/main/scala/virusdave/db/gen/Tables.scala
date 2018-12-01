package virusdave.db.gen
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = virusdave.db.SlickPgProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = ScratchTestTable.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table ScratchTestTable
   *  @param somePkey Database column some_pkey SqlType(serial), AutoInc, PrimaryKey
   *  @param someText Database column some_text SqlType(text), Default(None)
   *  @param someInteger Database column some_integer SqlType(int4), Default(None)
   *  @param someBoolean Database column some_boolean SqlType(bool), Default(false) */
  case class ScratchTestTableRow(somePkey: Int, someText: Option[String] = None, someInteger: Option[Int] = None, someBoolean: Boolean = false)
  /** GetResult implicit for fetching ScratchTestTableRow objects using plain SQL queries */
  implicit def GetResultScratchTestTableRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[Option[Int]], e3: GR[Boolean]): GR[ScratchTestTableRow] = GR{
    prs => import prs._
    ScratchTestTableRow.tupled((<<[Int], <<?[String], <<?[Int], <<[Boolean]))
  }
  /** Table description of table scratch_test_table. Objects of this class serve as prototypes for rows in queries. */
  class ScratchTestTable(_tableTag: Tag) extends profile.api.Table[ScratchTestTableRow](_tableTag, "scratch_test_table") {
    def * = (somePkey, someText, someInteger, someBoolean) <> (ScratchTestTableRow.tupled, ScratchTestTableRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(somePkey), someText, someInteger, Rep.Some(someBoolean)).shaped.<>({r=>import r._; _1.map(_=> ScratchTestTableRow.tupled((_1.get, _2, _3, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column some_pkey SqlType(serial), AutoInc, PrimaryKey */
    val somePkey: Rep[Int] = column[Int]("some_pkey", O.AutoInc, O.PrimaryKey)
    /** Database column some_text SqlType(text), Default(None) */
    val someText: Rep[Option[String]] = column[Option[String]]("some_text", O.Default(None))
    /** Database column some_integer SqlType(int4), Default(None) */
    val someInteger: Rep[Option[Int]] = column[Option[Int]]("some_integer", O.Default(None))
    /** Database column some_boolean SqlType(bool), Default(false) */
    val someBoolean: Rep[Boolean] = column[Boolean]("some_boolean", O.Default(false))
  }
  /** Collection-like TableQuery object for table ScratchTestTable */
  lazy val ScratchTestTable = new TableQuery(tag => new ScratchTestTable(tag))
}
