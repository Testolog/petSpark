package com.pet.test

import java.io.Closeable
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

/**
 * com.pet.test
 *
 * @author Robert Nad
 */
object Soft extends App {

  // 1 task
  case class NameNumber(name: String, phone: String)

  def convertor(delimiter: String): String => NameNumber = {
    def apply(string: String): NameNumber = {
      NameNumber(string.split(delimiter).head, string.split(delimiter).last)
    }

    apply
  }

  def findPersons[T](arrDiapason: Seq[T], key: T)(implicit ordering: Ordering[T]): Option[T] = {
    @tailrec
    def go(lower: Int, upper: Int): Option[Int] = {
      if (lower > upper)
        None
      else {
        val mid: Int = lower + (upper - lower) / 2
        math.signum(ordering.compare(key, arrDiapason(mid))) match {
          case -1 => go(lower, mid - 1)
          case 1 => go(mid + 1, upper)
          case _ => Some(mid)
        }
      }
    }

    go(0, arrDiapason.length - 1).map(arrDiapason(_))
  }

  implicit val ordering: Ordering[NameNumber] = (x: NameNumber, y: NameNumber) => x.name.compareTo(y.name)

  def autoClose[T <: Closeable, R](c: T): (T => R) => R = {
    def ff(f: T => R): R = {
      try {
        f(c)
      } finally {
        c.close()
      }
    }

    ff
  }

  val kk = NameNumber("23", "")
  val telephone = Range(0, 300).map(x => NameNumber(x.toString, (x * 4).toString)).grouped(50)
  telephone.flatMap(p => findPersons(p, kk).toList).foreach(println(_))
  // 2 task

  def getSynonyms(seqSynonyms: Seq[String]): String => Option[String] = {
    def f(string: String) = seqSynonyms.find(_.equals(string))

    f
  }

  val findSynomy = getSynonyms(Seq("a", "b", "c"))
  println(findSynomy("b"))
  println(findSynomy("f"))

  //  3 task
  def isPowerOf2(x: Int) =
    (x != 0) && ((x & (x - 1)) == 0)

  //  4 task
  //for db storing i would like to use of snowflake

  trait GeographyUnit {
    val name: String
  }

  case class Continent(name: String) extends GeographyUnit

  case class Country(name: String) extends GeographyUnit

  case class State(name: String) extends GeographyUnit

  case class City(name: String) extends GeographyUnit

  trait Tree[+A] {
    def fold[B](f: => B)(g: (A, B) => B): B =
      this match {
        case NodeGeography(v, c) => g(v, c.foldLeft(f)((a, b) => b.fold(a)(g)))
        case LastGeographyNode(v) => g(v, f)
      }
  }

  case class NodeGeography[V <: GeographyUnit](value: V, child: Seq[Tree[V]]) extends Tree[V]

  case class LastGeographyNode[V <: GeographyUnit](value: V) extends Tree[V]

  val city1 = Seq(City("c1"), City("c2"))
  val city2 = Seq(City("c3"), City("c4"))
  val stateTree = Seq(
    NodeGeography(State("s1"), city1.map(p => LastGeographyNode(p))),
    NodeGeography(State("s2"), city2.map(p => LastGeographyNode(p)))
  )
  val country = Seq(
    NodeGeography(Country("ct1"), Seq(stateTree.head)),
    NodeGeography(Country("ct2"), Seq(stateTree.last)),
  )

  val continent = NodeGeography(Continent("cnt"), country)
  println(continent.fold(0)((a, b) => {
    println(a, b)
    b + 1
  }))
  println(stateTree.map(c =>
    c.fold(0)((a, b) => {
      println(a, b)
      b + 1
    })
  ))

  // 5 task
  //  infinity recursion without conditional to out from method

  // 6 task
  trait Number

  case class IntNumber(value: Int) extends Number {
    override def toString: String = value.toString
  }

  case class DoubleNumber(value: Double) extends Number {
    override def toString: String = value.toString
  }

  def toNumeric[T](str: String): Try[Number] =
    Try(IntNumber(str.toInt))
      .recoverWith({ case _: NumberFormatException => Try(DoubleNumber(str.toDouble)) })

  def add[T <: Number](x: T, y: T): Number = (x, y) match {
    case (i: IntNumber, ii: IntNumber) => IntNumber(i.value + ii.value)
    case (i: IntNumber, d: DoubleNumber) => DoubleNumber(i.value + d.value)
    case (i: DoubleNumber, d: DoubleNumber) => DoubleNumber(i.value + d.value)
  }

  val first = "5" //args
  val sec = "4.6" //args
  toNumeric(first).flatMap(p => toNumeric(sec).map(y => add(p, y))) match {
    case Failure(exception) => exception.printStackTrace()
    case Success(value) => println(value)
  }
  // 7
  val l1 = Range(0, 10)
  val l2 = Range(0, 10, 2)
  println(l1.intersect(l2))
  println(l1.diff(l2))
  println(l2.diff(l1))
  println(l2.intersect(l2).toSet)

  // 8
  def avg[T](seq: Seq[T])(implicit numeric: Numeric[T]): Double = {
    @tailrec
    def go(prev: T, count: Long, st: Seq[T]): Double = {
      st match {
        case Nil => numeric.toDouble(prev) / count
        case x :: tail => go(numeric.plus(prev, x), count + 1, tail)
      }
    }

    go(seq.head, 1, seq.tail)
  }

  println(avg(Range(0, 100).toList))

  //9
  class Superhero extends Serializable {
    private val name = null
    private val debut = null
    private val numVillainsFought = 0
    private val numAlterEgos = 0
    private val masked = false
    private val female = false
    private val retired = false

  }

}
