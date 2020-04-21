package com.pet.spark

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, ExecutionContextExecutor, Future}

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
object UnpersistJob extends App {

  implicit val ectx: ExecutionContextExecutor = ExecutionContext.global

  def a(x: Int): Future[Int] = Future {
    x
  }

  def b(x: Future[Int]): Future[String] = x.map(p => p.toString + "hello")

  def c: Int => Future[String] = a _ andThen b

  //  print(c(10))


  def div(d: Double): Future[Double] = Future {
    d / 2
  }


  def div2(s: Double, d: Double): Future[Double] = Future {
    s * d
  }


  def tryDiv(s: Double): Future[Double] = div(s) flatMap { x => div2(s, x) }

  val i = Future {
    2 * 10
  }.flatMap(p => tryDiv(p))

  val s = Future {
    2 * 10
  }
    .flatMap(p => div(p))

  println(Await.result(i, 5.second))

}
