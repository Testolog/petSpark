package com.pet.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming._

import scala.collection.mutable

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object StreamingProcess extends App {
  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)
  val dataQueue: mutable.Queue[RDD[Visit]] = new mutable.Queue[RDD[Visit]]()
  val streamingContext = new StreamingContext(new SparkConf().setAppName("stream tests")
    .setMaster("local[*]"), Seconds(1))

  streamingContext.checkpoint("/tmp/streaming")

  case class Visit(userId: Long, page: String, duration: Long)

  val visits = Seq(
    Visit(1, "home.html", 10), Visit(2, "cart.html", 5), Visit(1, "home.html", 10),
    Visit(2, "address/shipping.html", 10), Visit(2, "address/billing.html", 10)
  )
  visits.foreach(visit => dataQueue += streamingContext.sparkContext.makeRDD(Seq(visit)))
  println(dataQueue.size)

  def handleVisit(key: Long, visit: Option[Visit], state: State[Long]): Option[Any] = {
    (visit, state.getOption()) match {
      case (Some(newVisit), None) =>
        // the 1st visit
        state.update(newVisit.duration)
        None
      case (Some(newVisit), Some(totalDuration)) =>
        // next visit
        state.update(totalDuration + newVisit.duration)
        None
      case (None, Some(totalDuration)) =>
        // last state - timeout occurred and passed
        // value is None in this case
        Some(key, totalDuration)
      case _ => None
    }
  }

  // The state expires 4 seconds after the lasts seen entry for
  // given key. The schedule for our test will look like:
  // user1 -> 0+4, user2 -> 1+4, user1 -> 2+4, user2 -> 3+4, user2 -> 4+4
  val sessionsAccumulator = streamingContext.sparkContext.collectionAccumulator[(Long, Long)]("sessions")
  streamingContext.queueStream(dataQueue)
    .map(visit => (visit.userId, visit))
    .mapWithState(StateSpec.function(handleVisit _).timeout(Durations.seconds(1)))
    .foreachRDD(rdd => {
      val terminatedSessions =
        rdd.filter(_.isDefined).map(_.get.asInstanceOf[(Long, Long)]).collect()
      terminatedSessions.foreach(p => {
        println(p)
        sessionsAccumulator.add(p)
      })
    })

  streamingContext.start()
  streamingContext.awaitTerminationOrTimeout(1000)
  //  println(s"Terminated sessions are ${sessionsAccumulator.value}")
  //  println(sessionsAccumulator.value)
}
