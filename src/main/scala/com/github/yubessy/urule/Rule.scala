package com.github.yubessy.urule

import com.netaporter.uri.Uri

import types._

case class Rule(
  pattern: Pattern,
  action: Action
) {
  def extract(uri: Uri): Option[String] = {
    if (pattern.matchAll(uri)) action match {
      case Right(s) => Some(s)
      case Left(rules) => collectFirst(uri, rules)
    } else {
      None
    }
  }

  private def collectFirst(uri: Uri, rules: Seq[Rule]): Option[String] =
    rules.view.map(_.extract(uri)).collectFirst{ case Some(s) => s }
}

object Rule {
  def apply(m: RuleMap): Rule =
    (
      for {
        p <- m.get("pattern")
        a <- m.get("action")
      } yield Rule(makePattern(p), makeAction(a))
    ).getOrElse(throw new Exception("rule must have pattern and action"))

  def apply(s: Seq[RuleMap]): Rule = Rule(
    Pattern.any,
    makeSubRules(s)
  )

  private def makePattern(p: StringAnyMap): Pattern =
    p match {
      case p: Map[String, String] => Pattern(p)
      case _ => throw new Exception("pattern is wrong")
    }

  private def makeAction(a: StringAnyMap): Action =
    if (a.contains("return")) {
      a("return") match {
        case s: String => Right(s)
        case _ => throw new Exception("return value is wrong")
      }
    } else if (a.contains("rules")) {
      a("rules") match {
        case s: Seq[RuleMap] => makeSubRules(s)
        case _ => throw new Exception("rules format is wrong")
      }
    } else {
      throw new Exception("rule must contain rules or return")
    }

  private def makeSubRules(s: Seq[RuleMap]): Action =
    Left(s.map(Rule.apply))
}
