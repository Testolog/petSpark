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
      .map(_.getLines())
    new LinePerFileIterator(files).foreach(println)
  }

  class LinePerFileIterator(inputIterators: Array[Iterator[String]]) extends Iterator[String] {
    private var tmpIter = inputIterators.iterator

    override def hasNext: Boolean = {
      val anyElement = inputIterators.exists(_.hasNext)
      if (anyElement && !tmpIter.hasNext) {
        tmpIter = inputIterators.filter(_.hasNext).iterator
      }
      anyElement
    }

    override def next(): String = {
      tmpIter.next().next()
    }
  }

}
