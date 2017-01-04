package com.github.yubessy.urule

import com.netaporter.uri.Uri

import types._

case class Rule(
  pattern: Pattern,
  action: Action
) {
  def extract(uri: Uri): Option[String] = {
    if (pattern.matchTo(uri)) action match {
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
  def apply(m: RuleMap): Rule = Rule(
    makePattern(m("pattern")),
    makeAction(m("action"))
  )

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
