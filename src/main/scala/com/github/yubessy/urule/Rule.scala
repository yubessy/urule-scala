package com.github.yubessy.urule

import com.netaporter.uri.Uri

import types._

case class Rule(
  pattern: Pattern,
  action: Action
) {
  def applyTo(uri: Uri): Option[String] = {
    if (pattern.matchTo(uri)) action match {
      case Right(s) => Some(s)
      case Left(rules) => applyToMany(uri, rules)
    } else {
      None
    }
  }

  private def applyToMany(uri: Uri, rules: Seq[Rule]): Option[String] =
    rules.view.map(_.applyTo(uri)).collectFirst{ case Some(s) => s }
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
    p match { case p: Map[String, String] => Pattern(p) }

  private def makeAction(a: StringAnyMap): Action =
    if (a.contains("return")) {
      a("return") match { case s: String => Right(s) }
    } else {
      a("rules") match { case s: Seq[RuleMap] => makeSubRules(s) }
    }

  private def makeSubRules(s: Seq[RuleMap]): Action =
    Left(s.map(Rule.apply))
}
