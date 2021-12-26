package com.pet.actors

import akka.actor.{Actor, ActorRef, Props}

/**
 * com.pet.actors
 *
 * @author Robert Nad
 */
class FilterActor[T](next: ActorRef, fun: T => Boolean) extends Actor {
  override def receive: Receive = {
    case msg: T if fun(msg) => next ! msg
  }
}

object FilterActor {

  def apply[T](next: ActorRef, fun: T => Boolean) = {
    Props(new FilterActor[T](next, fun))
  }
}