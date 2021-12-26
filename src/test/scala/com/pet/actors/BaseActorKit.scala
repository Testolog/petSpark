package com.pet.actors

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

/**
 * com.pet.actors
 *
 * @author Robert Nad
 */
trait BaseActorKit extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }
}
