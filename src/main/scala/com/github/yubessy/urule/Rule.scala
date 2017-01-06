package com.github.yubessy.urule

import com.netaporter.uri.Uri

import types.StringAnyMap

case class Rule(
  pattern: Pattern,
  action: Action
) {
  def extract(uri: Uri): Option[String] =
    if (pattern.matchAll(uri)) action.invoke(uri) else None
}

object Rule {
  def apply(m: StringAnyMap): Rule =
    (
      for {
        p <- m.get("pattern").collect { case m: StringAnyMap => m }
        a <- m.get("action").collect { case m: StringAnyMap => m }
      } yield Rule(Pattern(p), Action(a))
    ).getOrElse(throw new Exception("rule must have both pattern and action"))

  def apply(s: Seq[StringAnyMap]): Rule = Rule(
    Pattern(Map.empty),
    Action(Map("rules" -> s))
  )
}
