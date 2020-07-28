package com.pet.task

import scala.collection.mutable
import scala.io.Source

/**
 *
 * @author Robert Nad
 */
object ChunkRead {
  val buffer = new Array[Array[String]](5)


  def main(args: Array[String]): Unit = {
    val files = args.head.split(",")
      .map(Source.fromFile)
      .map(_.getLines())
    val mergedFiles = new MergeFilesIterator[String, Iterator[String]](files, compareDate)
    new AggregateIterator[String](mergedFiles, sum, equalDate).foreach(println)
  }

  class MergeFilesIterator[Val, Iter <: Iterator[Val]](inputIterators: Array[Iter],
                                                       comparatorFunction: (Val, Val) => Int)
    extends Iterator[Val] {

    override def hasNext: Boolean = {
      inputIterators.exists(_.hasNext)
    }

    private val allFiles: mutable.ArrayBuffer[(Iter, Val)] = new mutable.ArrayBuffer[(Iter, Val)](inputIterators.length) {
      inputIterators.filter(_.hasNext).foreach(g => append((g, g.next())))
    }


    override def next(): Val = {
      var returnValue = null.asInstanceOf[Val]
      var indexForUpdate = 0
      for (current <- allFiles.zipWithIndex) {
        val currentVal = current._1._2
        val index = current._2
        if (returnValue == null) {
          returnValue = currentVal
          indexForUpdate = index
        } else {
          if (comparatorFunction(returnValue, currentVal) > 0) {
            returnValue = currentVal
            indexForUpdate = index
          }
        }
      }
      val (iter, _) = allFiles(indexForUpdate)
      if (iter.hasNext) {
        allFiles.update(indexForUpdate, (iter, iter.next()))
      } else {
        allFiles.remove(indexForUpdate)
      }
      returnValue
    }
  }

  def sum(str1: String, str2: String): String = {
    val value1 = str1.split(":").last
    val value2 = str2.split(":").last
    "%s:%s".format(str1.split(":").head, value1.toInt + value2.toInt)
  }

  def equalDate(str1: String, str2: String): Boolean = {
    val key1 = str1.split(":").head
    val key2 = str2.split(":").head
    key1.equals(key2)
  }

  def compareDate(str1: String, str2: String): Int = {
    val date1 = str1.split(":").head.replace("-", "").toInt
    val date2 = str2.split(":").head.replace("-", "").toInt
    date1.compareTo(date2)
  }

  class AggregateIterator[T](inputIterators: Iterator[T],
                             aggregationFunction: (T, T) => T,
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
          compressed = aggregationFunction(compressed, vCurrent)
          vCurrent = virtual.next()
          real.next()
        }
        //check, last and previous are equal
        if (!virtual.hasNext) {
          if (conditionalFunction(compressed, vCurrent)) {
            real.next()
            compressed = aggregationFunction(compressed, vCurrent)
          }
        }
        compressed
      } else {
        current
      }
    }
  }

}
