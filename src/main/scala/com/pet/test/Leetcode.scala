package com.pet.test

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
      //      x.toString
      if (next == null) {
        x.toString
      } else {
        x.toString + "->" + next.toString
      }
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

  private var ints: Array[Int] = Array(1, 2, 3)
  for {
    a <- ints
  }
    removeDuplicates(Array(1, 1, 2))

  //psd.*ppdsa*
  //"-2"AnyAll
  //"-1"Any
  //"1"Equal
  //"2"ZeroOrAll

  sealed trait Pattern

  case object AnyZeroOrMany extends Pattern

  case object Any extends Pattern

  case class Equal(value: Char) extends Pattern

  case class ZeroOrMany(value: Char) extends Pattern

  sealed trait Result

  case object MoveStringPattern extends Result

  case object MoveString extends Result

  case object MovePattern extends Result

  case object FailMatching extends Result

  case class MatchFlow(data: Pattern, next: Option[MatchFlow]) {
    def checkMatching(c: Char): Result = {
      data match {
        case AnyZeroOrMany => next match {
          case Some(value) => value.checkMatching(c) match {
            case MoveStringPattern | MoveString => MovePattern
            case _ => MoveString
          }
          case None => MoveString
        }
        case Any => MoveStringPattern
        case Equal(value) => if (c == value) MoveStringPattern else FailMatching
        case ZeroOrMany(value) => if (c == value) MoveString else MovePattern
      }
    }

    def effectOnLength(): Int = data match {
      case AnyZeroOrMany => 0
      case Any => 1
      case Equal(_) => 1
      case ZeroOrMany(_) => 0
    }

    def changeNext(next: Option[MatchFlow]): MatchFlow = MatchFlow(this.data, next)

    def simplifyFlow(): MatchFlow = next match {
      case Some(futureElement) => (data, futureElement.data) match {
        case (ZeroOrMany(current), Equal(future)) if current == future =>
          MatchFlow(data, futureElement.next).simplifyFlow()
        case _ =>
          this.changeNext(Some(futureElement.simplifyFlow()))
      }
      case None => this
    }

  }

  def processMatch(p: List[Char]): Option[MatchFlow] = p match {
    case Nil => None
    case head :: Nil if isChar(head) => Some(MatchFlow(Equal(head), None))
    case head :: Nil if head == 46 => Some(MatchFlow(Any, None))
    case head :: future :: tail =>
      if (isChar(head)) {
        if (isChar(future) | future == 46) Some(MatchFlow(Equal(head), processMatch(future +: tail)))
        else Some(MatchFlow(ZeroOrMany(head), processMatch(tail)))
      }
      else {
        if (head == 46 && future == 42) Some(MatchFlow(AnyZeroOrMany, processMatch(tail)))
        else Some(MatchFlow(Any, processMatch(future +: tail)))
      }
  }

  def minimumLength(matchFlow: MatchFlow, expect: Int): Int = matchFlow.next match {
    case Some(value) => minimumLength(value, expect + matchFlow.effectOnLength())
    case None => expect + matchFlow.effectOnLength()
  }

  def isMatch(s: String, p: String): Boolean = {
    if (s.isEmpty)
      return p.isEmpty | p.contains(".*") | !p.contains(".")
    if (p.isEmpty & s.nonEmpty)
      return false
    var matchFlow: Option[MatchFlow] = processMatch(p.toList)
    val min = minimumLength(matchFlow.get, 0)
    matchFlow = matchFlow.map(_.simplifyFlow())
    var next = true
    var stringIndex = 0
    var processedLength = 0
    while (next && stringIndex < s.length) {
      val current = s(stringIndex)
      matchFlow match {
        case Some(value) =>
          value.checkMatching(current) match {
            case MoveStringPattern =>
              stringIndex += 1
              matchFlow = value.next
              processedLength += 1
            case MoveString =>
              stringIndex += 1
              processedLength += 1
            case MovePattern =>
              matchFlow = value.next
            case FailMatching =>
              next = false
          }
        case None =>
          next = false
      }
    }
    matchFlow match {
      case Some(value) => value.data match {
        case AnyZeroOrMany =>
          if (value.next.isDefined) false
          else processedLength >= min & (s.length - processedLength) == 0
        case _ => processedLength >= min & (s.length - processedLength) == 0
      }
      case None => processedLength >= min & (s.length - processedLength) == 0
    }
  }

  def isChar(b: Char): Boolean = b >= 97 & b <= 122

  def fastAnalysis(string: String, p: Seq[(Char, Int)]) = {
    p.count(p => Math.abs(p._2) == 1) <= string.length
  }

  //  println(isMatch("aaa", "a"))
  //  println(isMatch("sdasdadb", ".*b"))
  //  println(isMatch("aaa", "a.a"))
  //  println(isMatch("aa", "a"))
  //  println(isMatch("aa", "aaa"))
  //  println(isMatch("a", "ab*a"))
  //  println(isMatch("ab", ".*c"))
  //  println(isMatch("abcddcdccb", ".*c.*b"))
  //  println(isMatch("abbbaca", "ab*bbbaca"))
  //  println(isMatch("aaa", "a*a"))
  println(isMatch("abgdede", "ab.*de"))
  //Any
  //abStart
  //any_lenght
  //deEnd
  // .*sa.*sa
  println(isMatch("sasa", ".*sa.*sa"))
  println(isMatch("sasasasasa", ".*sa.*sa"))
  println(isMatch("sadddssa", ".*sa.*sa"))
  //  println(isMatch("aa", "a"))
  //  println(isMatch("mississippi", "mis*is*p*."))
  //  println(isMatch("aab", "c*a*b"))
  //  println(isMatch("adddddbcab", "a.*bcab"))
  //aaaaaaaa
  //b*
  // c*
  // d*
  // s*
  //pp*pb*s
  //psd.*ppdsa*
  //psd .*
  //ppds a*
  //psdffddffddffdpds
  //psd true
  // ffddffddffd true
  //pds false

  //  def p(c: List[Char]): Unit = {
  //    c match {
  //      case Nil => println("nill")
  //      case x :: Nil => println(s"nill $x")
  //      case x :: y :: tail => {
  //        println(s"$x, $y")
  //        p(y +: tail)
  //      }
  //    }
  //  }

  //  p("psd.*ppdsa*".toList)
}

