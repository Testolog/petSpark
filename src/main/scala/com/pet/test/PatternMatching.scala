package com.pet.test


/**
 * com.pet.test
 *
 * @author Robert Nad
 */
object PatternMatching extends App {

  sealed trait PatternType

  case class Any(len: Int) extends PatternType //.

  case object AnyMany extends PatternType //.*

  case class EndWithSubString(sub: String) extends PatternType //.*\w

  case class EndWithRepeatChar(char: Char, minimum: Int) extends PatternType //.*\w*

  case class RepeatChar(sub: Char, minimum: Int) extends PatternType //\w{m}

  case class EqualSubString(sub: String) extends PatternType //\w

  case class MatchingFlow(data: PatternType, next: Option[MatchingFlow]) {

    def changeNext(next: MatchingFlow): MatchingFlow = MatchingFlow(data, Some(next))

    def simplify(): MatchingFlow = {
      //щоб уникнути ситуації коли в кінці стрічки є значення яке зустрічалось поцентру але перед ним вже було значення
      //спробувати створити мапу значеннь які повинні зустрітись під час аналізу, і при маню патттерну аналізувати на кількість одиниць менше
      next match {
        case Some(future) =>
          (data, future.data) match {
            case (EqualSubString(c), EqualSubString(f)) => MatchingFlow(EqualSubString(c + f), future.next).simplify()
            case (RepeatChar(sc, len), EqualSubString(sf)) if sf.length == 1 && sc == sf.head => MatchingFlow(RepeatChar(sc, len + 1), future.next).simplify()
            case (RepeatChar(sc, clen), RepeatChar(sf, flen)) if sc == sf => MatchingFlow(RepeatChar(sc, clen + flen), future.next).simplify()
            case (RepeatChar(sc, clen), EndWithSubString(sf)) => MatchingFlow(EndWithSubString((sc.toString * clen) + sf), future.next).simplify()
            case (EndWithRepeatChar(sc, clen), EndWithSubString(sf)) => MatchingFlow(EndWithSubString((sc.toString * clen) + sf), future.next).simplify()
            case (EndWithSubString(sc), EqualSubString(sf)) => MatchingFlow(EndWithSubString(sc + sf), future.next).simplify()
            case (AnyMany, EqualSubString(f)) => MatchingFlow(EndWithSubString(f), future.next).simplify()
            case (AnyMany, RepeatChar(sf, len)) => MatchingFlow(EndWithRepeatChar(sf, len), future.next).simplify()
            case (Any(clen), Any(flen)) => MatchingFlow(Any(clen + flen), future.next).simplify()
            case _ => changeNext(future.simplify())
          }
        case None => this
      }
    }
  }


  def parseFlow(s: List[Char]): Option[MatchingFlow] = s match {
    case Nil => None
    case head :: Nil if isChar(head) => Some(MatchingFlow(EqualSubString(head.toString), None))
    case head :: Nil if head == 46 => Some(MatchingFlow(Any(1), None))
    case head :: future :: tail =>
      if (isChar(head)) {
        if (isChar(future) | future == 46)
          Some(MatchingFlow(EqualSubString(head.toString), parseFlow(future +: tail)))
        else
          Some(MatchingFlow(RepeatChar(head, 0), parseFlow(tail)))
      }
      else {
        if (head == 46 && future == 42) Some(MatchingFlow(AnyMany, parseFlow(tail)))
        else Some(MatchingFlow(Any(1), parseFlow(future +: tail)))
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
    def go(pattern: Option[MatchingFlow], reducedString: String, collectedString: String, lastRepeat: String, res: Boolean = true): Boolean = {
      if (!res) {
        return false
      }
      println(reducedString, collectedString)
      pattern match {
        case Some(value) =>
          value.data match {
            case Any(len) =>
              if (reducedString.length >= len) go(value.next, reducedString.substring(len), collectedString + reducedString.substring(0, len), lastRepeat)
              else false
            case EndWithSubString(sub) => getEndSubStringIndex(reducedString, sub) match {
              case Some(index) =>
                val processed = reducedString.substring(index).grouped(sub.length).takeWhile(_.equals(sub)).toList.size
                go(value.next, reducedString.substring(index + (processed * sub.length)), collectedString + reducedString.substring(0, index + (processed * sub.length)), lastRepeat)
              case None => false
            }
            case EndWithRepeatChar(sub, len) => getEndSubStringIndex(reducedString, sub.toString) match {
              case Some(index) =>
                val processed = reducedString.substring(index).takeWhile(sub == _).toList.size
                go(value.next, reducedString.substring(index).substring(processed), collectedString + reducedString.substring(0, index + processed), lastRepeat = sub.toString, res = processed >= len - 1)
              case None => go(value.next, reducedString, collectedString, lastRepeat)
            }
            case RepeatChar(char, minimum) =>
              val processed = reducedString.takeWhile(_ == char).toList.size
              go(value.next, reducedString.substring(processed), collectedString + reducedString.substring(0, processed), lastRepeat = if (processed > 0) char.toString else lastRepeat, res = processed >= minimum)
            case EqualSubString(sub) =>
              if (reducedString.length >= sub.length)
                go(value.next, reducedString.substring(sub.length), collectedString + reducedString.substring(0, sub.length), lastRepeat, res = reducedString.startsWith(sub))
              else lastRepeat.nonEmpty & sub.startsWith(lastRepeat)
            case AnyMany =>
              if (collectedString.isEmpty) go(value.next, reducedString, collectedString, lastRepeat)
              else go(value.next, "", collectedString + reducedString, lastRepeat)
          }
        case None =>
          reducedString.isEmpty & collectedString.equals(globalString)
      }
    }

    go(globalPattern, globalString, "", "")
  }


  def isMatch(s: String, p: String): Boolean = {
    if (s.isEmpty)
      return p.isEmpty | p.equals(".*") | !p.equals(".")
    if (p.isEmpty & s.nonEmpty)
      return false
    if (p.equals(".*"))
      return true
    val pattern = parseFlow(p.toList).map(_.simplify())
    matching(pattern, s)
  }

  //  val v = parseFlow("ab.*ab.*ab".toCharArray.toList).map(_.simplify())

  println("s" * 0)
  val equal = "23"
  val ss = "12345"
  println(ss.substring(0, 1))
  //  println(ss.grouped(2).takeWhile(!z_.equals("34")).toList)

  //  println(getEndSubStringIndex(ss, equal, 0) match {
  //    case Some(value) => ss.substring(value)
  //    case None => "bbad"
  //  })
  //  println(isMatch("abffffffabddddab", "ab.*ab.*ab"))
  //  println(isMatch("sda", ".*sda.*"))
  //  println(isMatch("asaa", ".*sda.*"))
  //  println(isMatch("bbbaass", "b*.*s"))
  //  println(isMatch("bbbaassa", "b*.*s*s"))
  //  println(isMatch("aaaaaaaaaaaacaaa", "ab*a*.*c*a"))
  println(isMatch("abfadfa", ".*..."))

  //  println(isMatch("a", "ab*a"))
  //  println(isMatch("mississippi", "mis*is*ip*."))

  //a->[
  //  b=>a->E
  //  a=>a->E
  //  any=>a->E
  //  c=>a->E
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
}
