package com.pet.test

/**
 * com.pet.test
 *
 * @author Robert Nad
 */
object Soft extends App {


  def drop1(l: List[T forSome {
    type T
  }]) = l.tail

  drop1(List(1, 2, 3, 4))

  trait Container[M[_]] {
    def put[A](x: A): M[A]

    def get[A](m: M[A]): A
  }

  def foo(x: {def get: Int}) = 123 + x.get

  val container = new Container[List] {
    def put[A](x: A) = List(x)

    def get[A](m: List[A]): A = m.head
  }

  trait Foo[M[_]] {
    type t[A] = M[A]
  }

  class A[T](t: T) {
    val p = t

    override def toString: String = t.toString
  }

  val x: Foo[A]#t[String] = new A("asd")

  class B extends (Int => Int) {
    override def apply(v1: Int): Int = 0
  }

  val b = new B
  println(b(1))

  class AAdd {
    def app[T](a1: A[T], a2: A[T]): String =
      a1.p.toString + a2.p.toString
  }

  implicit val d: AAdd = new AAdd


}
