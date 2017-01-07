package com.github.yubessy.urule

import com.netaporter.uri.Uri

case class Action(
  returns: Option[Map[String, String]],
  subRules: Option[Seq[Rule]]
) {
  def invoke(uri: Uri): Option[Map[String, String]] = {
    subRules.flatMap(rules => extractByFirst(uri, rules)).orElse(returns)
  }

  private def extractByFirst(uri: Uri, rules: Seq[Rule]): Option[Map[String, String]] =
    rules.view.map(_.extract(uri)).collectFirst { case Some(s) => s }
}

object Action {
  def apply(m: Map[String, _]): Action = {
    val returns = m.get("return").collect {
      case m: Map[String, String] => m
    }
    val subRules = m.get("rules").collect {
      case s: Seq[Map[String, _]] => s.map(Rule.apply)
    }
    if (returns.isEmpty && subRules.isEmpty) {
      throw new Exception("Action must contain either rules or return")
    } else {
      Action(returns, subRules)
    }
  }
}
