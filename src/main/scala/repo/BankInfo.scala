package repo

import db.{DbComponent, PostgresDbComponent}

import scala.concurrent.Future

case class BankInfo(owner: String, branches: Int, bankId: Int, id: Option[Long] = None)

object BankInfoRepository extends BankInfoRepository with BankInfoReadRepositoryImpl
  with BankInfoWriteRepositoryImpl with DbComponent with BankInfoTable with PostgresDbComponent

trait BankInfoRepository extends BankInfoWriteRepository with BankInfoReadRepository

// =====================================================================================================================
// Write Repository
// =====================================================================================================================

trait BankInfoWriteRepository {
  def store(info: BankInfo): Future[Int]

  def update(info: BankInfo): Future[Int]
}

trait BankInfoWriteRepositoryImpl extends BankInfoWriteRepository {
  this: DbComponent with BankInfoTable =>

  import driver.api._

  def store(info: BankInfo): Future[Int] =
    db.run(bankInfoTableQuery += info)

  def update(info: BankInfo): Future[Int] =
    db.run(bankInfoTableQuery.filter(_.id === info.id).update(info))

}


// =====================================================================================================================
// Read Repository
// =====================================================================================================================

trait BankInfoReadRepository {
  def getAllBankWithInfo: Future[List[(Bank, Option[BankInfo])]]

  def getBankWithInfo: Future[List[(Bank, BankInfo)]]
}

trait BankInfoReadRepositoryImpl extends BankInfoReadRepository {
  this: DbComponent with BankInfoTable =>

  import driver.api._

  /**
    * Get bank and info using foreign key relationship
    */
  def getBankWithInfo(): Future[List[(Bank, BankInfo)]] =
    db.run(
      (for {
        bank <- bankTableQuery
        info <- bankInfoTableQuery
      } yield (bank, info)).to[List].result
    )

  /**
    * Get all bank and their info.It is possible some bank do not have their product
    */
  def getAllBankWithInfo: Future[List[(Bank, Option[BankInfo])]] =
    db.run(
      (for {
        (bank, bankInfo) <- bankTableQuery joinLeft bankInfoTableQuery on (_.id === _.bankId)
      } yield (bank, bankInfo)).to[List].result
    )

}

trait BankInfoTable extends BankTable {
  this: DbComponent =>

  import driver.api._

  val bankInfoTableQuery: TableQuery[BankInfoSlickMapping] = TableQuery[BankInfoSlickMapping]

  class BankInfoSlickMapping(tag: Tag) extends Table[BankInfo](tag, "bank_info") {

    val id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    val owner: Rep[String] = column[String]("name", O.Unique)
    val branches: Rep[Int] = column[Int]("branches")
    val bankId: Rep[Int] = column[Int]("bank_id")

    def bank = foreignKey("bank_id_fk", bankId, bankTableQuery)(_.id)

    override def * = (owner, branches, bankId, id.?) <> (BankInfo.tupled, BankInfo.unapply)
  }

}
