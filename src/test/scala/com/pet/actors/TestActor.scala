package com.pet.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import com.pet.actors.SendingActor.EventMessages
import org.scalatest.{Matchers, WordSpecLike}

import scala.util.Random

/**
 * com.pet.actors
 *
 * @author Robert Nad
 */
class TestActor extends TestKit(ActorSystem("test-system")) with Matchers with WordSpecLike with BaseActorKit {

  "A Silent Actor" should {
    "change state when it receives a message, single threaded" in {
      import SilentActor._
      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper")
      silentActor.underlyingActor.state should (contain("whisper"))
    }
    "change state when it receives a message, multi-threaded" in {
      import SilentActor._
      val silentActor = system.actorOf(Props[SilentActor](), "hello")
      silentActor ! SilentMessage("whisper")
      silentActor ! GetState(testActor)
      expectMsg(Vector[String]("whisper"))
    }
  }
  "A Sending Actor" should {
    "send a message to another actor when it has finished processing" in {
      import SendingActor._
      val props = SendingActor.props(testActor)
      val sendingActor = system.actorOf(props, "sendingActor")
      val size = 1000
      val maxInclusive = 100000

      def randomEvents() = (0 until size).map {
        _ => Event(Random.nextInt(maxInclusive))
      }.toVector

      val unsorted = randomEvents()
      val sortEvents = SortEvents(unsorted)
      sendingActor ! sortEvents
      expectMsgPF() {
        case SortedEvents(events) =>
          events.size should be(size)
          unsorted.sortBy(_.id) should be(events)
      }
    }
    "filter actors" in {
      val filterActor = system.actorOf(FilterActor(testActor, (x: EventMessages) => x.data.startsWith("no")))
      val silentActor = system.actorOf(SendingActor.props(filterActor))
      silentActor ! EventMessages("no")
      silentActor ! EventMessages("world")
      silentActor ! EventMessages("hello world")
      expectMsgPF() {
        case msg =>
          println(msg)
      }
    }
  }
}
