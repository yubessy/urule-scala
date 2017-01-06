package com.github.yubessy.urule

import com.github.yubessy.urule.types.{RuleMap, StringAnyMap}
import com.netaporter.uri.Uri

case class Action(
  returns: Option[String],
  subRules: Option[Seq[Rule]]
) {
  def invoke(uri: Uri): Option[String] = {
    subRules match {
      case Some(rules) => rules.view.map(_.extract(uri)).collectFirst{ case Some(s) => s }
      case None => returns
    }
  }
}

object Action {
  def apply(m: StringAnyMap): Action = {
    val returns = m.get("return") match {
      case Some(s: String) => Some(s)
      case _ => None
    }
    val subRules = m.get("rules") match {
      case Some(s: Seq[RuleMap]) => Some(s.map(Rule.apply))
      case _ => None
    }
    if (returns.isEmpty && subRules.isEmpty) {
      throw new Exception("rule must contain rules or return")
    } else {
      Action(returns, subRules)
    }
  }
}
