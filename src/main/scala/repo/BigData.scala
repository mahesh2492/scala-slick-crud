package repo

import db.{DbComponent, PostgresDbComponent}
import slick.collection.heterogeneous.HNil

import scala.concurrent.Future

case class Large(
                  a: Int, b: Int, c: Int, d: Int,
                  e: Int, f: Int, g: Int, h: Int,
                  i: Int, j: Int, k: Int, l: Int,
                  m: Int, n: Int, o: Int, p: Int,
                  q: Int, r: Int, s: Int, t: Int,
                  u: Int, v: Int, w: Int, x: Int,
                  y: Int, z: Int
                )

object LargeRepository extends LargeRepository with DbComponent with PostgresDbComponent with BigDataTable


trait LargeRepository {
  this: DbComponent with BigDataTable =>

  import driver.api._

  def create =
    db.run(bigDataTableQuery.schema.create)

  def store(l: Large) =
    db.run(bigDataTableQuery += l)

  def getData: Future[Seq[Large]] =
    db.run(bigDataTableQuery.result)
}


trait BigDataTable {
  this: DbComponent =>

  import driver.api._

  lazy val bigDataTableQuery = TableQuery[BigDataTableMapping]

  class BigDataTableMapping(tag: Tag) extends Table[Large](tag, "large") {
    def a = column[Int]("a")
    def b = column[Int]("b")
    def c = column[Int]("c")
    def d = column[Int]("d")
    def e = column[Int]("e")
    def f = column[Int]("f")
    def g = column[Int]("g")
    def h = column[Int]("h")
    def i = column[Int]("i")
    def j = column[Int]("j")
    def k = column[Int]("k")
    def l = column[Int]("l")
    def m = column[Int]("m")
    def n = column[Int]("n")
    def o = column[Int]("o")
    def p = column[Int]("p")
    def q = column[Int]("q")
    def r = column[Int]("r")
    def s = column[Int]("s")
    def t = column[Int]("t")
    def u = column[Int]("u")
    def v = column[Int]("v")
    def w = column[Int]("w")
    def x = column[Int]("x")
    def y = column[Int]("y")
    def z = column[Int]("z")

    def * = (
      a :: b :: c :: d ::
        e :: f :: g :: h ::
        i :: j :: k :: l ::
        m :: n :: o :: p ::
        q :: r :: s :: t ::
        u :: v :: w :: x ::
        y :: z :: HNil
      ).mapTo[Large]

  }

}