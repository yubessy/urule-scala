package com.github.yubessy.urule

import com.github.yubessy.urule.types.StringAnyMap
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
  def apply(m: StringAnyMap): Action = {
    val returns = m.get("return").collect { case s: String => s}
    val subRules = m.get("rules").collect {
      case s: Seq[StringAnyMap] => s.map(Rule.apply)
    }
    if (returns.isEmpty && subRules.isEmpty) {
      throw new Exception("rule must contain rules or return")
    } else {
      Action(returns, subRules)
    }
  }
}
