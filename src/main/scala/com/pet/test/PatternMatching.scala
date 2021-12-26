package com.pet.test


/**
 * com.pet.test
 *
 * @author Robert Nad
 */
object PatternMatching extends App {
  sealed trait Location

  case object Start extends Location

  case object Middle extends Location

  case object End extends Location

  sealed trait PatternType

  case class Any(len: Int) extends PatternType

  case object AnyMany extends PatternType //.*

  case class LengthChecker(len: Int) extends PatternType

  case class EndWithSubString(sub: String, skipLength: Int) extends PatternType //.*\w

  case class EndWithRepeatChar(char: Char, skipLength: Int, minimum: Int) extends PatternType //.*\w*

  case class RepeatChar(sub: Char, minimum: Int) extends PatternType //\w{m}

  case class EqualSubString(sub: String) extends PatternType

  case class FlowControl(current: String,
                         processed: String,
                         step: String,
                         pattern: Option[PatternType],
                         prev: Option[FlowControl]) {

    def updateStatus(reduce: Int, append: Int, patternType: PatternType): FlowControl = {
      val localStep = current.substring(0, append)
      if (localStep.nonEmpty) FlowControl(
        current.substring(reduce),
        processed + localStep,
        localStep,
        Option(patternType),
        Option(this)
      ) else this
    }
  }


  case class MatchingFlow(data: PatternType, next: Option[MatchingFlow], location: Location) {

    def calLoc(location1: Location, location2: Location): Location = (location1, location2) match {
      case (Start, Middle) => Start
      case _ => location2
    }

    def simplify(): MatchingFlow = {
      //щоб уникнути ситуації коли в кінці стрічки є значення яке зустрічалось поцентру але перед ним вже було значення
      //спробувати створити мапу значеннь які повинні зустрітись під час аналізу, і при маню патттерну аналізувати на кількість одиниць менше

      next match {
        case Some(future) =>
          val flow = ((p: PatternType) => MatchingFlow(p, future.next, calLoc(location, future.location))).andThen(_.simplify())
          (data, future.data) match {
            case (EqualSubString(c), EqualSubString(f)) => flow(EqualSubString(c + f))
            case (RepeatChar(sc, len), EqualSubString(sf)) if sf.length == 1 && sc == sf.head => flow(RepeatChar(sc, len + 1))
            case (RepeatChar(sc, clen), RepeatChar(sf, flen)) if sc == sf => flow(RepeatChar(sc, clen + flen))
            case (RepeatChar(sc, clen), EndWithSubString(sf, skipLength)) => flow(EndWithSubString((sc.toString * clen) + sf, skipLength))
            case (EndWithRepeatChar(sc, cskipLength, clen), EndWithSubString(sf, fskipLength)) => flow(EndWithSubString((sc.toString * clen) + sf, cskipLength + fskipLength))
            case (EndWithRepeatChar(sc, skipLength, clen), EqualSubString(sf)) => flow(EndWithSubString((sc.toString * clen) + sf, skipLength))
            case (EndWithSubString(sc, skipLength), EqualSubString(sf)) => flow(EndWithSubString(sc + sf, skipLength))
            case (Any(clen), Any(flen)) => flow(Any(clen + flen))
            case (AnyMany, EqualSubString(f)) => flow(EndWithSubString(f, 0))
            case (AnyMany, RepeatChar(sf, len)) => flow(EndWithRepeatChar(sf, len, 0))
            case (AnyMany, Any(clen)) => flow(LengthChecker(clen))
            case (AnyMany, AnyMany) => flow(AnyMany)
            case (LengthChecker(clen), Any(flen)) => flow(LengthChecker(clen + flen))
            case (LengthChecker(clen), EqualSubString(sub)) => flow(EndWithSubString(sub, clen))
            case (LengthChecker(cLen), RepeatChar(sub, minimum)) => flow(EndWithRepeatChar(sub, cLen, minimum))
            case (LengthChecker(cLen), AnyMany) => flow(LengthChecker(cLen))
            case _ => MatchingFlow(data, next.map(_.simplify()), calLoc(location, future.location))
          }
        case None => this
      }
    }
  }


  def parseFlow(s: List[Char], isStart: Boolean = false): Option[MatchingFlow] = s match {
    case Nil => None
    case head :: Nil if isChar(head) => Some(MatchingFlow(EqualSubString(head.toString), None, End))
    case head :: Nil if head == 46 => Some(MatchingFlow(Any(1), None, End))
    case head :: future :: tail =>
      if (isChar(head)) {
        if (isChar(future) | future == 46)
          Some(MatchingFlow(EqualSubString(head.toString), parseFlow(future +: tail), if (isStart) Start else Middle))
        else
          Some(MatchingFlow(RepeatChar(head, 0), parseFlow(tail), if (isStart) Start else Middle))
      }
      else {
        if (head == 46 && future == 42) Some(MatchingFlow(AnyMany, parseFlow(tail), if (isStart) Start else Middle))
        else Some(MatchingFlow(Any(1), parseFlow(future +: tail), if (isStart) Start else Middle))
      }
  }

  def isChar(b: Char): Boolean = b >= 97 & b <= 122


  def getEndSubStringIndex(base: String, equal: String, index: Int = 0): Option[Int] = {
    if (index + equal.length - 1 >= base.length)
      return None
    if (!base.substring(index, equal.length + index).equals(equal)) {
      getEndSubStringIndex(base, equal, index + 1)
    } else {
      Some(index + equal.length)
    }
  }

  def matching(globalPattern: Option[MatchingFlow], globalString: String): Boolean = {
    def go(pattern: Option[MatchingFlow], flowControl: FlowControl, isPassed: Boolean = true): Boolean = {
      if (!isPassed) {
        return false
      }
      println(flowControl.current, flowControl.processed)
      pattern match {
        case Some(value) =>
          value.data match {
            case Any(len) =>
              if (flowControl.current.length >= len) go(value.next, flowControl.updateStatus(len, len, value.data))
              else false
            case RepeatChar(char, minimum) =>
              val processed = flowControl.current.takeWhile(_ == char).toList.size
              go(value.next, flowControl.updateStatus(processed, processed, value.data), isPassed = processed >= minimum)
            case EqualSubString(sub) =>
              if (flowControl.current.length >= sub.length) {
                go(value.next, flowControl.updateStatus(sub.length, sub.length, value.data), isPassed = flowControl.current.startsWith(sub))
              } else if (sub.length == 1) flowControl.pattern.exists {
                case RepeatChar(ss, _) => ss == sub.head
                case _ => false
              } else false
            // ANY to Many
            case EndWithSubString(sub, skipLength) => getEndSubStringIndex(flowControl.current, sub, skipLength) match {
              case Some(index) =>
                val subSequence = flowControl.current.substring(index)
                val flow = value.location match {
                  case End => if (subSequence.endsWith(sub))
                    flowControl.updateStatus(index + subSequence.length, index + subSequence.length, value.data)
                  else
                    flowControl.updateStatus(index, index, value.data)
                  case _ => flowControl.updateStatus(index, index, value.data)
                }
                go(value.next, flow)
              case None => false
            }
            case EndWithRepeatChar(sub, skipLength, len) => getEndSubStringIndex(flowControl.current, sub.toString, skipLength) match {
              case Some(index) =>
                val processed = flowControl.current.substring(index).takeWhile(sub == _).toList.size
                go(value.next, flowControl.updateStatus(index + processed, index + processed, value.data), isPassed = processed >= len - 1)
              case None =>
                value.location match {
                  case End => go(value.next, flowControl.updateStatus(flowControl.current.length, flowControl.current.length, value.data))
                  case _ => skipLength > 0 & flowControl.current.length >= skipLength
                }
            }
            case LengthChecker(len) => go(value.next,
              flowControl.updateStatus(flowControl.current.length, flowControl.current.length, value.data),
              isPassed = flowControl.current.length >= len)
            case AnyMany => go(value.next, flowControl.updateStatus(flowControl.current.length, flowControl.current.length, value.data))
          }
        case None =>
          flowControl.current.isEmpty & flowControl.processed.equals(globalString)
      }
    }

    go(globalPattern, FlowControl(globalString, "", "", None, None))
  }


  //  def isMatch(s: String, p: String): Boolean = {
  //    if (s.isEmpty)
  //      return p.isEmpty | p.equals(".*") | !p.equals(".")
  //    if (p.isEmpty & s.nonEmpty)
  //      return false
  //    if (p.equals(".*"))
  //      return true
  //    val pattern = parseFlow(p.toList, isStart = true).map(_.simplify())
  //    matching(pattern, s)
  //  }

  //  val v = parseFlow("ab.*ab.*ab".toCharArray.toList).map(_.simplify())

  println("s" * 0)
  val equal = "23"
  val ss = "12345"
  println(ss.substring(0, 1))
  println("aaaab".takeWhile(_ == 'a').toList)
  //    println(ss.grouped(2).takeWhile(!z_.equals("34")).toList)

  //  println(getEndSubStringIndex(ss, equal, 0) match {
  //    case Some(value) => ss.substring(value)
  //    case None => "bbad"
  //  })
  //  println(isMatch("abffffffabddddab", "ab.*ab.*ab"))
  //  println(isMatch("a", "ab*a"))
  //  println(isMatch("sda", ".*sda.*"))
  //  println(isMatch("asaa", ".*sda.*"))
  //  println(isMatch("bbbaass", "b*.*s"))
  //  println(isMatch("bbbaassa", "b*.*s*s"))
  //  println(isMatch("aaba", "ab*a*c*a"))
  //  println(isMatch("abfadfabgbbbgg", ".*....*a*b.*g*g"))
  //  println(isMatch("ab", ".*..c*"))
  //  println(isMatch("aasdfasdfasdfasdfas", "aasdf.*asdf.*asdf.*asdf.*s"))


  //aaaaaaa, ...a*
  //  println(isMatch("abgdede", "ab.*de"))
  //  println(isMatch("a", "ab*a"))
  //  println(isMatch("mississippi", "mis*is*ip*."))

  //a->[
  //  b=>a->E
  //  a=>a->E
  //  any=>a->E
  //  c=>a->E
  // ]
  // a->[

  // ]
  //  def generateFlow():
  // shift, reduce
  // ab.*ab.*ab
  // abasdaabababab
  //
  /*
  .*sda.*
  .* start any
  a middle
  .* end any
  ab*bbbab
  ab
  b*
  bbab
  abbbbbbbbbab

  a start shift
  bbbab end, shift



  if start = any
     start = not_any symbol
  if end = any
      ignore




  * ab append start
  * asda shift middle
  * ab append middle
  * abab shift middle
  * ab append end
  *
  * */


  var memo: Array[Array[Boolean]] = null

  def isMatch(text: String, pattern: String): Boolean = {
    memo = new Array[Array[Boolean]](text.length + 1)
    memo(0) = new Array[Boolean](pattern.length + 1)
    dp(0, 0, text, pattern)
  }

  def dp(i: Int, j: Int, text: String, pattern: String): Boolean = {
    if (memo(i)(j) != null) {
      return memo(i)(j)
    }
    var ans: Boolean = false
    if (j == pattern.length) {
      ans = i == text.length
    }
    else {
      val first_match: Boolean = (i < text.length && (pattern.charAt(j) == text.charAt(i) || pattern.charAt(j) == '.'))
      if (j + 1 < pattern.length && pattern.charAt(j + 1) == '*') {
        ans = (dp(i, j + 2, text, pattern) || first_match && dp(i + 1, j, text, pattern))
      }
      else {
        ans = first_match && dp(i + 1, j + 1, text, pattern)
      }
    }
    memo(i)(j) = ans
    ans
  }

  println(isMatch("abcddcdccb", ".*c.*b"))
}
