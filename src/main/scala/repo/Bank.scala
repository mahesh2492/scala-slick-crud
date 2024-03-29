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

  def bulkInsert(banks: List[Bank]): Future[Option[Int]]

}

trait BankWriteRepositoryImpl extends BankWriteRepository {
  this: DbComponent with BankTable =>

  import driver.api._

  def store(bank: Bank): Future[Int] =
    db.run(bankTableQuery += bank)

  def update(bank: Bank): Future[Int] =
    db.run(bankTableQuery.filter(_.id === bank.id).update(bank))

  def bulkInsert(banks: List[Bank]): Future[Option[Int]] =
    db.run(bankTableQuery ++= banks)

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
    db.run(bankTableQuery.filter(_.id === id).result.headOption)

  def list: Future[List[Bank]] =
    db.run(bankTableQuery.to[List].result)

}

trait BankTable {
  this: DbComponent =>

  import driver.api._

  val bankTableQuery: TableQuery[BankSlickMapping] = TableQuery[BankSlickMapping]

  class BankSlickMapping(tag: Tag) extends Table[Bank](tag, "bank") {

    val id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    val name: Rep[String] = column[String]("name", O.Unique)

    override def * = (name, id.?) <> (Bank.tupled, Bank.unapply)
  }

}
