import repo.{Bank, BankRepository, Large, LargeRepository}

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

  val bankRepository = BankRepository

  def initialData: List[Bank] = {
    List(
      Bank("Sb1"),
      Bank("Canara"),
      Bank("Pnb"),
      Bank("Bank of India"),
      Bank("Central Bank of India"),
      Bank("Idbi Bank"),
      Bank("Bank of Baroda")
    )
  }

  val insertedRows =
  for {
    rowsAffected <- bankRepository.bulkInsert(initialData)
  } yield rowsAffected

  insertedRows.foreach { e =>
    println(s"# rows have been inserted, $insertedRows")
  }

  Thread.sleep(5000)

  (for {
    bank <- bankRepository.list
  } yield bank).map { bank =>
    println(s"Bank Information: $bank")
  }

  LargeRepository.create

  private val large = Large(
    1, 2, 3, 4, 5, 6, 7, 8, 9,
    1, 2, 3, 4, 5, 6, 7, 8, 9,
    1, 2, 3, 4, 5, 6, 7, 8
  )

  (for {
    _ <- LargeRepository.store(large)
    _ = Thread.sleep(2000)
    l <- LargeRepository.getData
  } yield l).foreach { e =>
    println(s"Got the large data: $e")
  }

  Thread.sleep(5000)
}
