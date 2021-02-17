package com.pet.akka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import java.nio.file.Paths
import scala.concurrent.duration.DurationInt

/**
 * com.pet.spark
 *
 * @author Robert Nad
 */
object CyclenDataToKafka extends App {
  implicit val system: ActorSystem = ActorSystem("KafkaService")

  def flowWriteToKafka(settings: ProducerSettings[String, String]): Sink[String, NotUsed] = Flow
    .fromFunction[String, (String, String)](p => {
      val kv = p.split(":")
      (kv.head, kv.last)
    })
    .map(p => {
      ProducerMessage.single(new ProducerRecord("new_data", 0, p._1, p._2))
    })
    .via(Producer.flexiFlow(settings))
    .map {
      case ProducerMessage.Result(metadata, message) =>
        s"${metadata.topic}/${metadata.partition} ${metadata.offset}: ${message.record}"
      case ProducerMessage.PassThroughResult(passThrough) =>
        s"passed through"
    }
    .to(Sink.foreach(println(_)))

  val cfgString =
    """
      |akka.kafka.producer {
      |  use-dispatcher = "akka.kafka.default-dispatcher"
      |  close-timeout=10s
      |  close-on-producer-stop = true
      |  parallelism = 100
      |  eos-commit-interval = 100ms
      |  kafka-clients {
      |  }
      |}
      |""".stripMargin
  val producerSettings: ProducerSettings[String, String] = ProducerSettings(
    ConfigFactory
      .parseString(cfgString)
      .getConfig("akka.kafka.producer"),
    new StringSerializer,
    new StringSerializer
  ).withBootstrapServers("localhost:9092")

  val (queue, aggregationSource) = Source
    .queue[String](12, OverflowStrategy.backpressure)
    .throttle(4, 2.second)
    .wireTap(flowWriteToKafka(producerSettings))
    .preMaterialize()

  FileIO
    .fromPath(Paths.get("./test1.txt"))
    .via(Framing.delimiter(ByteString("\n"), 1048576))
    .map(_.utf8String)
    .via(
      Flow[String]
        .merge(aggregationSource)
        .map(p => queue.offer(p))
    )
    .run()
}
