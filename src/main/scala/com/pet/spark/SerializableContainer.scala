package com.pet.spark


/**
 * com.pet.spark
 *
 * using with broadcast, this way will return single instance and threadsafe per each execution
 * val bc = sparkSession.sparkContext.broadcast(new SerializableContainer[T]{
 * def create(): null.asInstance[T]
 * })
 * val value = bc.value.containerContent
 *
 * @author Robert Nad
 */
abstract class SerializableContainer[T] extends Serializable {

  def create(): T

  @transient lazy val containerContent: T = create()

}
