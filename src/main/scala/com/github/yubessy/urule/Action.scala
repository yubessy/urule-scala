package com.github.yubessy.urule

import com.netaporter.uri.Uri

case class Action(
  returns: Option[String],
  subRules: Option[Seq[Rule]]
) {
  def invoke(uri: Uri): Option[String] = {
    subRules.flatMap(rules => extractByFirst(uri, rules)).orElse(returns)
  }

  private def extractByFirst(uri: Uri, rules: Seq[Rule]): Option[String] =
    rules.view.map(_.extract(uri)).collectFirst { case Some(s) => s }
}

object Action {
  def apply(m: Map[String, _]): Action = {
    val returns = m.get("return").collect { case s: String => s}
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
