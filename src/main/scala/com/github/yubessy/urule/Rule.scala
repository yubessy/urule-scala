package com.github.yubessy.urule

import com.netaporter.uri.Uri

import types._

case class Rule(
  pattern: Pattern,
  action: Action
) {
  def extract(uri: Uri): Option[String] = {
    if (pattern.matchAll(uri)) action.invoke(uri) else None
  }
}

object Rule {
  def apply(m: RuleMap): Rule =
    (
      for {
        p <- m.get("pattern")
        a <- m.get("action")
      } yield Rule(Pattern(p), Action(a))
    ).getOrElse(throw new Exception("rule must have pattern and action"))

  def apply(s: Seq[RuleMap]): Rule = Rule(
    Pattern(Map.empty),
    Action(Map("rules" -> s))
  )
}
