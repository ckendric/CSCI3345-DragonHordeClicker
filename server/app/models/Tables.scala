package models
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Hoard.schema ++ Hoardupgrade.schema ++ Univupgrades.schema ++ Users.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Hoard
   *  @param hoardId Database column hoard_id SqlType(serial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(int4)
   *  @param hoardtype Database column hoardtype SqlType(int4)
   *  @param cost Database column cost SqlType(int4)
   *  @param hoardlevel Database column hoardlevel SqlType(int4)
   *  @param hoarditems Database column hoarditems SqlType(float8)
   *  @param productionspeed Database column productionspeed SqlType(float8)
   *  @param goldconversionrate Database column goldconversionrate SqlType(float8)
   *  @param unlocked Database column unlocked SqlType(bool) */
  case class HoardRow(hoardId: Int, userId: Int, hoardtype: Int, cost: Int, hoardlevel: Int, hoarditems: Double, productionspeed: Double, goldconversionrate: Double, unlocked: Boolean)
  /** GetResult implicit for fetching HoardRow objects using plain SQL queries */
  implicit def GetResultHoardRow(implicit e0: GR[Int], e1: GR[Double], e2: GR[Boolean]): GR[HoardRow] = GR{
    prs => import prs._
    HoardRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Double], <<[Double], <<[Double], <<[Boolean]))
  }
  /** Table description of table hoard. Objects of this class serve as prototypes for rows in queries. */
  class Hoard(_tableTag: Tag) extends profile.api.Table[HoardRow](_tableTag, "hoard") {
    def * = (hoardId, userId, hoardtype, cost, hoardlevel, hoarditems, productionspeed, goldconversionrate, unlocked) <> (HoardRow.tupled, HoardRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(hoardId), Rep.Some(userId), Rep.Some(hoardtype), Rep.Some(cost), Rep.Some(hoardlevel), Rep.Some(hoarditems), Rep.Some(productionspeed), Rep.Some(goldconversionrate), Rep.Some(unlocked))).shaped.<>({r=>import r._; _1.map(_=> HoardRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column hoard_id SqlType(serial), AutoInc, PrimaryKey */
    val hoardId: Rep[Int] = column[Int]("hoard_id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int4) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column hoardtype SqlType(int4) */
    val hoardtype: Rep[Int] = column[Int]("hoardtype")
    /** Database column cost SqlType(int4) */
    val cost: Rep[Int] = column[Int]("cost")
    /** Database column hoardlevel SqlType(int4) */
    val hoardlevel: Rep[Int] = column[Int]("hoardlevel")
    /** Database column hoarditems SqlType(float8) */
    val hoarditems: Rep[Double] = column[Double]("hoarditems")
    /** Database column productionspeed SqlType(float8) */
    val productionspeed: Rep[Double] = column[Double]("productionspeed")
    /** Database column goldconversionrate SqlType(float8) */
    val goldconversionrate: Rep[Double] = column[Double]("goldconversionrate")
    /** Database column unlocked SqlType(bool) */
    val unlocked: Rep[Boolean] = column[Boolean]("unlocked")

    /** Foreign key referencing Users (database name hoard_user_id_fkey) */
    lazy val usersFk = foreignKey("hoard_user_id_fkey", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Hoard */
  lazy val Hoard = new TableQuery(tag => new Hoard(tag))

  /** Entity class storing rows of table Hoardupgrade
   *  @param hoardupgradeId Database column hoardupgrade_id SqlType(serial), AutoInc, PrimaryKey
   *  @param hoardId Database column hoard_id SqlType(int4)
   *  @param upgradeno Database column upgradeno SqlType(int4)
   *  @param cost Database column cost SqlType(int4)
   *  @param unlocked Database column unlocked SqlType(bool)
   *  @param newspeed Database column newspeed SqlType(float8)
   *  @param goldmultiplier Database column goldmultiplier SqlType(float8) */
  case class HoardupgradeRow(hoardupgradeId: Int, hoardId: Int, upgradeno: Int, cost: Int, unlocked: Boolean, newspeed: Double, goldmultiplier: Double)
  /** GetResult implicit for fetching HoardupgradeRow objects using plain SQL queries */
  implicit def GetResultHoardupgradeRow(implicit e0: GR[Int], e1: GR[Boolean], e2: GR[Double]): GR[HoardupgradeRow] = GR{
    prs => import prs._
    HoardupgradeRow.tupled((<<[Int], <<[Int], <<[Int], <<[Int], <<[Boolean], <<[Double], <<[Double]))
  }
  /** Table description of table hoardupgrade. Objects of this class serve as prototypes for rows in queries. */
  class Hoardupgrade(_tableTag: Tag) extends profile.api.Table[HoardupgradeRow](_tableTag, "hoardupgrade") {
    def * = (hoardupgradeId, hoardId, upgradeno, cost, unlocked, newspeed, goldmultiplier) <> (HoardupgradeRow.tupled, HoardupgradeRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(hoardupgradeId), Rep.Some(hoardId), Rep.Some(upgradeno), Rep.Some(cost), Rep.Some(unlocked), Rep.Some(newspeed), Rep.Some(goldmultiplier))).shaped.<>({r=>import r._; _1.map(_=> HoardupgradeRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column hoardupgrade_id SqlType(serial), AutoInc, PrimaryKey */
    val hoardupgradeId: Rep[Int] = column[Int]("hoardupgrade_id", O.AutoInc, O.PrimaryKey)
    /** Database column hoard_id SqlType(int4) */
    val hoardId: Rep[Int] = column[Int]("hoard_id")
    /** Database column upgradeno SqlType(int4) */
    val upgradeno: Rep[Int] = column[Int]("upgradeno")
    /** Database column cost SqlType(int4) */
    val cost: Rep[Int] = column[Int]("cost")
    /** Database column unlocked SqlType(bool) */
    val unlocked: Rep[Boolean] = column[Boolean]("unlocked")
    /** Database column newspeed SqlType(float8) */
    val newspeed: Rep[Double] = column[Double]("newspeed")
    /** Database column goldmultiplier SqlType(float8) */
    val goldmultiplier: Rep[Double] = column[Double]("goldmultiplier")

    /** Foreign key referencing Hoard (database name hoardupgrade_hoard_id_fkey) */
    lazy val hoardFk = foreignKey("hoardupgrade_hoard_id_fkey", hoardId, Hoard)(r => r.hoardId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Hoardupgrade */
  lazy val Hoardupgrade = new TableQuery(tag => new Hoardupgrade(tag))

  /** Entity class storing rows of table Univupgrades
   *  @param univupgradeId Database column univupgrade_id SqlType(serial), AutoInc, PrimaryKey
   *  @param userId Database column user_id SqlType(int4)
   *  @param upgradetype Database column upgradetype SqlType(int4)
   *  @param unlocked Database column unlocked SqlType(bool) */
  case class UnivupgradesRow(univupgradeId: Int, userId: Int, upgradetype: Int, unlocked: Boolean)
  /** GetResult implicit for fetching UnivupgradesRow objects using plain SQL queries */
  implicit def GetResultUnivupgradesRow(implicit e0: GR[Int], e1: GR[Boolean]): GR[UnivupgradesRow] = GR{
    prs => import prs._
    UnivupgradesRow.tupled((<<[Int], <<[Int], <<[Int], <<[Boolean]))
  }
  /** Table description of table univupgrades. Objects of this class serve as prototypes for rows in queries. */
  class Univupgrades(_tableTag: Tag) extends profile.api.Table[UnivupgradesRow](_tableTag, "univupgrades") {
    def * = (univupgradeId, userId, upgradetype, unlocked) <> (UnivupgradesRow.tupled, UnivupgradesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(univupgradeId), Rep.Some(userId), Rep.Some(upgradetype), Rep.Some(unlocked))).shaped.<>({r=>import r._; _1.map(_=> UnivupgradesRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column univupgrade_id SqlType(serial), AutoInc, PrimaryKey */
    val univupgradeId: Rep[Int] = column[Int]("univupgrade_id", O.AutoInc, O.PrimaryKey)
    /** Database column user_id SqlType(int4) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column upgradetype SqlType(int4) */
    val upgradetype: Rep[Int] = column[Int]("upgradetype")
    /** Database column unlocked SqlType(bool) */
    val unlocked: Rep[Boolean] = column[Boolean]("unlocked")

    /** Foreign key referencing Users (database name univupgrades_user_id_fkey) */
    lazy val usersFk = foreignKey("univupgrades_user_id_fkey", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Univupgrades */
  lazy val Univupgrades = new TableQuery(tag => new Univupgrades(tag))

  /** Entity class storing rows of table Users
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param username Database column username SqlType(varchar), Length(20,true)
   *  @param password Database column password SqlType(varchar), Length(200,true)
   *  @param gold Database column gold SqlType(int4) */
  case class UsersRow(id: Int, username: String, password: String, gold: Int)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Int], e1: GR[String]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Int], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table users. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends profile.api.Table[UsersRow](_tableTag, "users") {
    def * = (id, username, password, gold) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(username), Rep.Some(password), Rep.Some(gold))).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column username SqlType(varchar), Length(20,true) */
    val username: Rep[String] = column[String]("username", O.Length(20,varying=true))
    /** Database column password SqlType(varchar), Length(200,true) */
    val password: Rep[String] = column[String]("password", O.Length(200,varying=true))
    /** Database column gold SqlType(int4) */
    val gold: Rep[Int] = column[Int]("gold")
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}
