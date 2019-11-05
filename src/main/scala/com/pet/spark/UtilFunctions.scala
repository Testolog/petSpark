package com.pet.spark

import scala.annotation.tailrec

/**
  * com.pet.spark
  *
  * @author Robert Nad
  */
class UtilFunctions {
  def binarySearch[T](arrDiapason: Array[(T, T)], key: Comparable[T]): Option[Int] = {
    @tailrec
    def _binarySearch(lo: Int, hi: Int): Option[Int] = {
      if (lo > hi)
        None
      else {
        val mid: Int = lo + (hi - lo) / 2
        arrDiapason(mid) match {
          case dip: (T, T) if key.compareTo(dip._1) > 0 && key.compareTo(dip._2) <= 0 => Some(mid)
          case dip: (T, T) if key.compareTo(dip._2) > 0 => _binarySearch(mid + 1, hi)
          case _ => _binarySearch(lo, mid - 1)
        }
      }
    }

    _binarySearch(0, arrDiapason.length - 1)
  }
}
