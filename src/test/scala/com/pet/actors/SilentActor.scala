package com.pet.actors

import akka.actor.{Actor, ActorRef, Props}

/**
 * com.pet.actors
 *
 * @author Robert Nad
 */
class SilentActor extends Actor {
  private var internalState: Vector[String] = Vector[String]()

  override def receive: Receive = {
    case SilentActor.SilentMessage(data) => internalState :+= data
    case SilentActor.GetState(receiver) => receiver ! internalState
  }

  def state: Seq[String] = internalState
}

object SilentActor {
  case class SilentMessage(data: String)

  case class GetState(receiver: ActorRef)

  def apply(): Props = Props(new SilentActor())
}
