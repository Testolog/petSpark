package com.pet.test

import java.util
import scala.annotation.tailrec

/**
 * com.pet.test
 *
 * @author Robert Nad
 */
object Leetcode extends App {
  def twoSum(nums: Array[Int], target: Int): Array[Int] = {
    val l = nums.length - 1

    @tailrec
    def go(start: Int, current: Int): Array[Int] = {
      if (start > l)
        Array.empty
      else {
        val sum = nums(start) + nums(current)
        if (sum == target) {
          Array(start, current)
        } else {
          if (current <= l - 1) {
            go(start, current + 1)
          } else {
            go(start + 1, start + 2)
          }
        }
      }
    }


    if (l >= 2)
      go(0, 1)
    else
      Array.empty[Int]
  }

  class ListNode(_x: Int = 0, _next: ListNode = null) {
    var next: ListNode = _next
    var x: Int = _x

    override def toString: String = {
      x.toString
      //      if (next == null) {
      //        x.toString
      //      } else {
      //        x.toString + "->" + next.toString
      //      }
    }
  }

  def addTwoNumbers(l1: ListNode, l2: ListNode): ListNode = {

    val resHead = l1
    var resNext = resHead
    var increment = 0
    var ll2 = l2
    while (resNext != null) {
      if (ll2 != null) {
        resNext.x += ll2.x + increment
        ll2 = ll2.next
      } else {
        resNext.x += increment
      }
      if (resNext.x >= 10) { // 18
        resNext.x = resNext.x % 10 // 8
        increment = 1
      } else {
        increment = 0
      }
      //last element
      if (resNext.next == null) {
        if (ll2 != null) {
          resNext.next = ll2
          ll2 = null
        } else {
          if (increment > 0) {
            resNext.next = new ListNode(increment)
            increment = 0
          }
        }
      }
      resNext = resNext.next
      //move to next
    }
    resHead
  }

  def lengthOfLongestSubstring(s: String): Int = {
    if (s.isEmpty)
      return 0
    val charSequence = s.toCharArray
    var resultstr = 0 //d v

    for (i <- Range(0, charSequence.length)) {
      var middleRes = charSequence(i).toString
      var isNext = true
      var index = i + 1
      while (isNext & index < charSequence.length) {
        val c = charSequence(index)
        if (!middleRes.contains(c)) {
          middleRes += c
        } else {
          isNext = false
        }
        index += 1
      }
      if (middleRes.length > resultstr) {
        resultstr = middleRes.length
      }
    }
    resultstr
  }


  def findMedianSortedArrays(nums1: Array[Int], nums2: Array[Int]): Double = {
    val nl1 = nums1.length
    val nl2 = nums2.length
    // потрібно вибрати аррей за снову, та взяти центральні елементи і змерджити
    val fullLength = nl1 + nl2
    val start = if (fullLength % 2 == 0) (fullLength / 2) - 1 else fullLength / 2
    val end = if (fullLength % 2 == 0) (fullLength / 2) else fullLength / 2
    var i1 = 0
    var i2 = 0
    var isNext = true
    var index = 0
    var prev = 0.0
    var res = 0.0
    println(start, end)
    while (isNext) {
      //хуже вже не буде
      val current = (i1 <= nl1 - 1 & nl1 != 0, i2 <= nl2 - 1 & nl2 != 0) match {
        case (true, true) =>
          Math.signum(nums1(i1).compareTo(nums2(i2))) match {
            case 1 =>
              res = nums2(i2)
              i2 += 1
              res
            case -1 =>
              res = nums1(i1)
              i1 += 1
              res
            case 0 =>
              res = nums1(i1)
              i1 += 1
              res
          }
        case (false, true) =>
          res = nums2(i2)
          i2 += 1
          res
        case (true, false) =>
          res = nums1(i1)
          i1 += 1
          res
        case (false, false) => prev
      }
      if (start == end) {
        if (index == start) {
          res = current
          isNext = false
        }
      } else {
        if (index == end) {
          res = (prev + current) / 2.0
          isNext = false
        }
      }
      index += 1
      prev = current
    }
    res
  }


  def isNumber(b: Byte): Boolean = {
    b >= 48 & b <= 57
  }

  def myAtoi(s: String): Int = {
    var arr: Array[Char] = Array.empty
    val iter = s.getBytes.iterator
    var chart: Int = 0
    var out: Int = 0
    var next = true
    while (next & iter.hasNext) {
      val b = iter.next()
      if (b != 32) {
        if ((b == 45 | b == 43) & chart == 0) {
          chart = (b.toChar + "1").toInt
        }
        else if (isNumber(b)) {
          if (chart == 0) {
            chart = 1
          }
          arr :+= b.toChar
          val res = arr.mkString.toLong
          if (res > Int.MaxValue) {
            next = false
            out = if (chart > 0) Int.MaxValue else Int.MinValue
          } else {
            out = res.toInt * chart
          }
        }
        else {
          next = false
        }
      } else if (chart != 0) {
        next = false
      }
    }
    out
  }

  def isPalindrome(x: Int): Boolean = {

    var input = x
    var reversedNum = 0
    while (input != 0) {
      val lastNumb = input % 10
      reversedNum = reversedNum * 10 + lastNumb
      input = input / 10
    }
    reversedNum == x
  }

  def removeDuplicates(nums: Array[Int]): Int = {
    var size = 0
    var prev: Int = nums(0)
    for (i <- 1 until nums.length) {
      if (prev != nums(i)) {
        size += 1
        nums.updated(size, nums(i))
      }
      prev = nums(i)
    }
    size
  }

  def maxArea(height: Array[Int]): Int = {
    def max(x1: Int, x2: Int, area: Int): Int = {
      if (x1 < x2) {
        if (height(x2) > height(x1))
          max(x1 + 1, x2, Integer.max(area, Integer.min(height(x2), height(x1)) * (x2 - x1)))
        else
          max(x1, x2 - 1, Integer.max(area, Integer.min(height(x2), height(x1)) * (x2 - x1)))
      } else {
        area
      }
    }

    max(0, height.length - 1, 0)
  }

  //  println(maxArea(Array(2, 3, 4, 5, 18, 17, 6)))

  def longestCommonPrefix(strs: Array[String]): String = {
    if (strs.length == 0)
      return ""
    if (strs.length == 1)
      return strs(0)
    var index = 1
    while (index < strs.length) {
      if (!strs(index).startsWith(strs(0))) {
        strs(0) = strs(0).substring(0, strs(0).length - 1)
        index = 1
      } else {
        index += 1
      }

    }
    strs(0)
  }

  //  def radixSort(nums: Array[Int], digits: Int): Unit = {
  //    var map = Map[Int, Int]()
  //    for (i <- (1 until digits + 1).reverse) {
  //      val exp = Math.pow(10, i)
  //      //      (-9 until 10).foreach(map.put(_, 0))
  //      for (n <- nums) {
  //        val int = ((n / exp) % 10).toInt
  //        map += (int, map.getOrElse(int, 0) + 1)
  //      }
  //      for (k <- -8 until 10) {
  //        map(k) += map(k - 1)
  //      }
  //
  //    }
  //
  //  }

  def threeSum(nums: Array[Int]): List[List[Int]] = {
    if (nums.length < 3) {
      return List.empty
    }
    if (nums.length == 3) {
      return if (nums(0) + nums(1) + nums(2) == 0) List(nums.toList) else List()
    }
    val generator: Iterator[(Int, Int, Int)] = new Iterator[(Int, Int, Int)]() {
      var (i, j, k) = (0, 1, 1)

      override def hasNext: Boolean = i <= nums.length - 4

      override def next(): (Int, Int, Int) = {
        if (k == nums.length - 1) {
          if (j == nums.length - 2) {
            i += 1
            j = i + 1
            k = j + 1
          } else {
            j += 1
            k = j + 1
          }
        } else {
          k += 1
        }
        (i, j, k)
      }
    }
    (for {
      i <- generator
      if nums(i._1) + nums(i._2) + nums(i._3) == 0
    } yield List(nums(i._1), nums(i._2), nums(i._3)).sorted)
      .toList
      .distinct
  }

  //  def threeSumClosest(nums: Array[Int], target: Int): Int = {
  //    if (nums.length < 3) {
  //      return 0
  //    }
  //    if (nums.length == 3) {
  //      return nums.sum
  //    }
  //    java.util.Arrays.sort(nums)
  //    val fDistance = (low: Int, mid: Int, hie: Int) => Math.abs((nums(low) + nums(mid) + nums(hie)) - target)
  //    val fMid = (low: Int, height: Int) => (low + ((height - low) / 2))
  //    val fResult = (low: Int, mid: Int, height: Int) => nums(low) + nums(mid) + nums(height)
  //    var low = 0
  //    var height = nums.length - 1
  //    var mid = fMid(low, height)
  //    //центральна точка при парному, блище до максимум, при не парному по центру
  //    //зменшення границь пошуку комбінацій
  //    // якщо число знаходиться в першій частині, то зміщуємо туди
  //    //    while (nums(low) + nums(mid) >= target && low < mid && mid < height) {
  //    //      height = mid
  //    //      mid = fMid(low, height)
  //    //    }
  //    //    // інакше в інший діапазон
  //    //    while (nums(mid) + nums(height) <= target && low < mid && mid < height) {
  //    //      low = mid
  //    //      mid = fMid(low, height)
  //    //    }
  //    var dist = fDistance(low, mid, height)
  //    val state = () => Seq(
  //      (0, fDistance(low, mid, height)),
  //      if (low < mid - 1) (-1, fDistance(low, mid - 1, height)) else (0, fDistance(low, mid, height)),
  //      if (low < mid - 1) (-2, fDistance(low + 1, mid, height)) else (0, fDistance(low, mid, height)),
  //      if (mid < height - 1) (1, fDistance(low, mid + 1, height)) else (0, fDistance(low, mid, height)),
  //      if (mid < height - 1) (2, fDistance(low, mid, height - 1)) else (0, fDistance(low, mid, height)),
  //    ).minBy(_._2)
  //    while (state()._1 <= dist && dist != 0 && (low < mid && mid < height)) {
  //      state() match {
  //        case (0, d) =>
  //          dist = 0
  //        case (-1, d) =>
  //          if (low < mid - 1) {
  //            mid -= 1
  //            dist = d
  //          } else {
  //            dist = 0
  //          }
  //        case (-2, d) =>
  //          if (low < mid - 1) {
  //            low += 1
  //            dist = d
  //          } else {
  //            dist = 0
  //          }
  //        case (1, d) =>
  //          if (mid < height - 1) {
  //            mid += 1
  //            dist = d
  //          } else {
  //            dist = 0
  //          }
  //        case (2, d) =>
  //          if (mid < height - 1) {
  //            height -= 1
  //            dist = d
  //          } else {
  //            dist = 0
  //          }
  //      }
  //    }
  //    fResult(low, mid, height)
  //  }
  //  threeSum(Array(-3, 1, -5, 2, -4, 2, -1, 1, -5, -1, 4)).foreach(println(_))
  //  threeSumClosest(Array(0, 0, 0, 0)).foreach(println(_))
  //  println(Math.ceil(Math.log(Math.abs(-34) + 1) / Mat.log(10)).toInt)
  //  threeSum(Array(-4, -2, 1, -5, -4, -4, 4, -2, 0, 4, 0, -2, 3, 1, -5, 0)).foreach(println(_))
  //lose
  def threeSumClosest(nums: Array[Int], target: Int): Int = {
    var res = 0
    var diff = Integer.MAX_VALUE
    // Sort the input array
    util.Arrays.sort(nums)
    for (lower <- 0 until nums.length - 2) {
      var mid = lower + 1
      var upper = nums.length - 1
      while (mid < upper) {
        val sum = nums(lower) + nums(mid) + nums(upper)
        if (sum == target) return sum
        else {
          val d = Math.abs(sum - target)
          if (d < diff) {
            res = sum
            diff = d
          }
          if (sum > target) upper -= 1
          else mid += 1
        }
      }
    }
    res
  }

  //  println(threeSumClosest(Array(-1, 2, 1, -4), 1) == 2) //2
  //  println(threeSumClosest(Array(1, 1, 1, 0), -100) == 2) //2
  //  println(threeSumClosest(Array(0, 2, 1, -3), 1) == 0) //0
  //  println(threeSumClosest(Array(1, 1, -1, -1, 3), 1) == 1) //1
  //  println(threeSumClosest(Array(1, 1, -1, -1, 3), -1) == (-1)) //-1
  //  println(threeSumClosest(Array(1, 1, -1, -1, 3), 3) == 3) //3
  //  println(threeSumClosest(Array(1, 2, 4, 8, 16, 32, 64, 128), 82) == 82) //82
  //  println(threeSumClosest(Array(1, 2, 5, 10, 11), 12) == 13) //13
  //  println(threeSumClosest(Array(-55, -24, -18, -11, -7, -3, 4, 5, 6, 9, 11, 23, 33), 0) == 0) //0
  val data = Map(
    "1" -> List(),
    "2" -> List("a", "b", "c"),
    "3" -> List("d", "e", "f"),
    "4" -> List("g", "h", "i"),
    "5" -> List("j", "k", "l"),
    "6" -> List("m", "n", "o"),
    "7" -> List("p", "q", "r", "s"),
    "8" -> List("t", "u", "v"),
    "9" -> List("w", "x", "y", "z"),
    "0" -> List(" "),
  )

  def letterCombinations(digits: String): List[String] = {
    if (digits.isEmpty)
      return List.empty
    if (digits.length == 1)
      return data(digits.head.toString)
    var res = data(digits.head.toString)
    for (number <- digits.tail) {
      var tmp = List[String]()
      for (symbols <- data(number.toString)) {
        for (r <- res) {
          tmp :+= r + symbols
        }
      }
      res = tmp
    }
    res
  }


  //працюю від некст елемента, знаходжу самій послідній елемент,
  //та почина збір в зворотньому порядку, збілушуючи каунтер щоразу
  //
  def deepRemove(head: ListNode, n: Int): (ListNode, Int) = {
    if (head == null) {
      (null, 1)
    } else {
      val elm = deepRemove(head.next, n)
      if (elm._2 == n) {
        (elm._1, elm._2 + 1)
      } else {
        //якщо це елемент який нас цікавить, то ми його лінкуємо,
        //та просуваємо вперед
        head.next = elm._1
        (head, elm._2 + 1)
      }
    }
  }

  def removeNthFromEnd(head: ListNode, n: Int): ListNode = {
    deepRemove(head, n)._1
  }

  def isValid(s: String): Boolean = {
    true
  }

  def merge(l1: ListNode, l2: ListNode, res: ListNode): ListNode = {
    if (l2 == null || l1 == null) {
      res.next = if (l1 == null) l2 else l1
      res
    } else {
      if (l1.x == l2.x) {
        res.next = l2
        merge(l1.next, l2.next, res)
      } else if (l1.x > l2.x) {
        res.next = l2
        merge(l1, l2.next, res)
      } else {
        res.next = l1
        merge(l1.next, l2, res)
      }
    }
  }

  def mergeTwoLists(l1: ListNode, l2: ListNode): ListNode = {
    if (l1 == null || l2 == null) {
      return if (l1 == null) l2 else l1
    }
    var resultNode: ListNode = null
    var pointerHeader: ListNode = resultNode
    var pointerL1 = l1
    var pointerL2 = l2
    //first elm
    if (pointerL1.x <= pointerL2.x) {
      resultNode = l1
      pointerL1 = l1.next
    } else {
      resultNode = l2
      pointerL2 = l2.next
    }
    pointerHeader = resultNode
    while (pointerL1 != null) {
      var resultNext: ListNode = null
      if (pointerL2 != null) {
        if (pointerL1.x == pointerL2.x) {
          val next = pointerL1.next
          val header = pointerL1
          val next2 = pointerL2.next
          resultNext = pointerL1
          resultNext.next = pointerL2
          resultNext = resultNext.next
          resultNext.next = next
          pointerL1 = resultNext.next
          pointerL2 = next2
          pointerHeader.next = header
        } else if (pointerL1.x > pointerL2.x) {
          pointerHeader.next = pointerL2
          pointerL2 = pointerL2.next
        } else {
          pointerHeader.next = pointerL1
          pointerL1 = pointerL1.next
        }
        pointerHeader = pointerHeader.next
      } else {
        pointerHeader.next = pointerL1
        pointerL1 = null
      }

    }
    if (pointerL2 != null) {
      if (pointerHeader == null) {
        resultNode = pointerL2
      } else {
        pointerHeader.next = pointerL2
        pointerHeader = pointerHeader.next
      }
    }
    resultNode
  }

  mergeTwoLists(new ListNode(2), new ListNode(1))
  //  mergeTwoLists(new ListNode(1, new ListNode(2, new ListNode(4))), new ListNode(1, new ListNode(3, new ListNode(4))))
  //  1,   2,   3,   4,  5,  6, 7, 8, 9, 10, 11, 12, 13
  //-55, -24, -18, -11, -7, -3, 4, 5, 6,  9, 11, 23, 33
  //low                         mid              h
  //                            mid ?
  // do it while low < mid < height or move low or height up distance
  // move mid in borders
  //-55 + 4 + 33 = -18
  //-55 + (-3) + 33 = -25
  //-55 + 5 + 33 = - 17
  //-55 + 6 + 33 = -16
  //-55 + 9 + 33 = -13
  //-55 + 11 + 33 = -11
  //-55 + 23 + 33 = 1
  // isRight for check direction where is going to height == true
  // move upper or lesser
  // check low or height get profit
  // l: -24 + -3 + 33 = 18
  // h: -55 + -3 + 23 = -35
  // if abs(l) - target < abs(h) - target
  //    move l
  // else
  //    move h
  // go to (move mid in borders)

  //List(0, 0, 0)
  //List(-4, 0, 4)
  //List(-5, 1, 4)
  //List(-2, 1, 1)
  //List(-2, -2, 4)
  //List(-4, 1, 3)
  //                     i   j                           k
  //  threeSum(Array(-1,0,1,0)).foreach(println)
  //  threeSum(Array(-2, 0, 1, 1, 2)).foreach(println(_))
  //             0, 1,  2,  3,  4
  //             i  j   j   j   k
  //                i   j   j   k
  //                    i   j   k
  //                    step
  //             i  j   k
  //8,2,10
  //1,3,4
  //0,2,3

}

