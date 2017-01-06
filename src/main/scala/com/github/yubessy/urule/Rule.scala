package com.github.yubessy.urule

import com.netaporter.uri.Uri

case class Rule(
  pattern: Pattern,
  action: Action
) {
  def extract(uri: Uri): Option[String] =
    if (pattern.matchAll(uri)) action.invoke(uri) else None
}

object Rule {
  def apply(m: Map[String, _]): Rule =
    (
      for {
        p <- m.get("pattern").collect { case m: Map[String, _] => m }
        a <- m.get("action").collect { case m: Map[String, _] => m }
      } yield Rule(Pattern(p), Action(a))
    ).getOrElse(
      throw new Exception("Rule must contain both pattern and action")
    )

  def apply(s: Seq[Map[String, _]]): Rule =
    Rule(Pattern(Map.empty), Action(Map("rules" -> s)))
}
