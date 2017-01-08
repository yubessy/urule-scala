package com.github.yubessy.urule

import com.netaporter.uri.Uri

case class Rule(
  pattern: Pattern,
  returns: Option[Map[String, String]],
  subRules: Option[Seq[Rule]]
) {
  def extract(uri: Uri, tmp: Option[Map[String, String]] = None): Option[Map[String, String]] =
    if (pattern.matchAll(uri)) invoke(uri, tmp) else None

  def invoke(uri: Uri, tmp: Option[Map[String, String]]): Option[Map[String, String]] = {
    val updated = (tmp ++ returns).reduceOption(_ ++ _)
    subRules.flatMap(rules => extractByFirst(uri, rules, updated)).orElse(updated)
  }

  private def extractByFirst(uri: Uri, rules: Seq[Rule], tmp: Option[Map[String, String]]): Option[Map[String, String]] =
    rules.view.map(_.extract(uri, tmp)).collectFirst { case Some(s) => s }
}

object Rule {
  def apply(m: Map[String, _]): Rule =
    (
      for (
        pattern <- m.get("pattern").collect { case m: Map[String, _] => Pattern(m) }
      ) yield {
        val returns = m.get("return").collect {
          case m: Map[String, String] => m
        }
        val subRules = m.get("rules").collect {
          case s: Seq[Map[String, _]] => s.map(Rule.apply)
        }
        if (returns.isEmpty && subRules.isEmpty) {
          throw new Exception("Rule must contain either return or rules")
        } else {
          Rule(pattern, returns, subRules)
        }
      }
    ).getOrElse(
      throw new Exception("Rule must contain pattern")
    )

  def apply(s: Seq[Map[String, _]]): Rule =
    Rule(Pattern(Map.empty), None, Some(s.map(Rule.apply)))
}
