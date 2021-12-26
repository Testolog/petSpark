package com.pet.actors

import akka.actor.{Actor, ActorRef, Props}

/**
 * com.pet.actors
 *
 * @author Robert Nad
 */
class SendingActor(receiver: ActorRef) extends Actor {

  import SendingActor._

  override def receive: Receive = {
    case SortEvents(unsorted) => receiver ! SortedEvents(unsorted.sortBy(_.id))
    case EventMessages(id) => receiver ! EventMessages(id)
  }
}

object SendingActor {
  def props(receiver: ActorRef) = Props(new SendingActor(receiver))

  case class Event(id: Long)

  case class EventMessages(data: String)

  case class SortEvents(unsorted: Vector[Event])

  case class SortedEvents(sorted: Vector[Event])

}