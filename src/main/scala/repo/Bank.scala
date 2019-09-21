package repo

import db.{DbComponent, PostgresDbComponent}

import scala.concurrent.Future

case class Bank(name: String, id: Option[Int] = None)

object BankRepository extends BankRepository with BankReadRepositoryImpl
  with BankWriteRepositoryImpl with DbComponent with BankTable with PostgresDbComponent

trait BankRepository extends BankWriteRepository with BankReadRepository

// =====================================================================================================================
// Write Repository
// =====================================================================================================================

trait BankWriteRepository {
  def store(bank: Bank): Future[Int]

  def update(bank: Bank): Future[Int]
}

trait BankWriteRepositoryImpl extends BankWriteRepository {
  this: DbComponent with BankTable =>

  import driver.api._

  def store(bank: Bank): Future[Int] = {
    val res = db.run(bankQuery += bank)
     import scala.concurrent.ExecutionContext.Implicits.global
    res.foreach(e => println("inserted" + e))
    res
  }

  def update(bank: Bank): Future[Int] =
    db.run(bankQuery.filter(_.id === bank.id).update(bank))

}

// =====================================================================================================================
// Read Repository
// =====================================================================================================================

trait BankReadRepository {
  def getBankById(id: Int): Future[Option[Bank]]

  def list: Future[List[Bank]]
}

trait BankReadRepositoryImpl extends BankReadRepository {
  this: DbComponent with BankTable =>

  import driver.api._

  def getBankById(id: Int): Future[Option[Bank]] =
    db.run(bankQuery.filter(_.id === id).result.headOption)

  def list: Future[List[Bank]] =
    db.run(bankQuery.to[List].result)

}

trait BankTable {
  this: DbComponent =>

  import driver.api._

  val bankQuery: TableQuery[BankSlickMapping] = TableQuery[BankSlickMapping]

  class BankSlickMapping(tag: Tag) extends Table[Bank](tag, "bank") {

    val id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name: Rep[String] = column[String]("name", O.Unique)

    override def * = (name, id.?) <> (Bank.tupled, Bank.unapply)
  }

}
