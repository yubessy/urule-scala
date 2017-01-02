package com.github.yubessy.urule

import com.netaporter.uri.Uri

object t {
  type Action = Either[Seq[Rule], String]
}
import t._

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
  def build(x: Any): Rule = x match {
    case m: Map[String, Map[String, Any]] => Rule(
      getPattern(m("pattern")),
      getAction(m("action"))
    )
    case s: Seq[Any] => Rule(
      Pattern.any,
      subRules(s)
    )
  }

  private def getPattern(p: Map[String, Any]): Pattern =
    p match { case p: Map[String, String] => Pattern.build(p) }

  private def getAction(a: Map[String, Any]): Action =
    if (a.contains("return")) {
      a("return") match { case s: String => Right(s) }
    } else {
      a("rules") match { case s: Seq[Any] => subRules(s) }
    }

  private def subRules(s: Seq[Any]): Action =
    Left(s.map(Rule.build))
}
