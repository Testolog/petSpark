package com.pet.task

import scala.io.Source

/**
 *
 * @author Robert Nad
 */
object ChunkRead {
  val buffer = new Array[Array[String]](5)


  def main(args: Array[String]): Unit = {
    val files = args.head.split(",").map(Source.fromFile)
      .map(p => new AggregateIterator(p.getLines(), sumCompress, conditionalToCompress))
    new LinePerFileIterator[String, AggregateIterator[String]](files).foreach(println)
  }

  class LinePerFileIterator[B, T <: Iterator[B]](inputIterators: Array[T]) extends Iterator[B] {
    private var tmpIter = inputIterators.iterator

    override def hasNext: Boolean = {
      val anyElement = inputIterators.exists(_.hasNext)
      if (anyElement && !tmpIter.hasNext) {
        tmpIter = inputIterators.filter(_.hasNext).iterator
      }
      anyElement
    }

    override def next(): B = {
      tmpIter.next().next()
    }
  }

  def sumCompress(str1: String, str2: String): String = {
    val value1 = str1.split(":").last
    val value2 = str2.split(":").last
    "%s:%s".format(str1.split(":").head, value1.toInt + value2.toInt)
  }

  def conditionalToCompress(str1: String, str2: String): Boolean = {
    val key1 = str1.split(":").head
    val key2 = str2.split(":").head
    key1.equals(key2)
  }

  class AggregateIterator[T](inputIterators: Iterator[T],
                             compressFunction: (T, T) => T,
                             conditionalFunction: (T, T) => Boolean) extends Iterator[T] {
    val (real, virtual) = inputIterators.duplicate
    if (virtual.hasNext) {
      virtual.next()
    }

    override def hasNext: Boolean = {
      real.hasNext
    }

    override def next(): T = {
      val current: T = real.next()
      var vCurrent: T = if (virtual.hasNext) {
        virtual.next()
      } else {
        null.asInstanceOf[T]
      }
      if (vCurrent != null && conditionalFunction(current, vCurrent)) {
        var compressed: T = current
        while (conditionalFunction(compressed, vCurrent) && real.hasNext && virtual.hasNext) {
          compressed = compressFunction(compressed, vCurrent)
          vCurrent = virtual.next()
          real.next()
        }
        //check, last and previous are equal
        if (!virtual.hasNext) {
          if (conditionalFunction(compressed, vCurrent)) {
            real.next()
            compressed = compressFunction(compressed, vCurrent)
          }
        }
        compressed
      } else {
        current
      }
    }
  }

}
