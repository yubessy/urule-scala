package com.github.yubessy.urule

import com.netaporter.uri.Uri

case class Rule(
  pattern: Pattern,
  returns: Option[Map[String, String]],
  subRules: Option[Seq[Rule]]
) {
  def extract(uri: Uri, tmp: Option[Map[String, String]] = None): Option[Map[String, String]] = {
    val matchResult = pattern.matchAll(uri)
    if (matchResult) {
      val updated = (tmp ++ returns).reduceOption(_ ++ _)
      subRules.flatMap(rules => extractByFirst(uri, rules, updated)).orElse(updated)
    } else {
      None
    }
  }

  private def extractByFirst(uri: Uri, rules: Seq[Rule], tmp: Option[Map[String, String]]): Option[Map[String, String]] =
    rules.view.map(_.extract(uri, tmp)).collectFirst { case Some(s) => s }
}

object Rule {
  def apply(m: Map[String, _]): Rule = {
    val pattern = Pattern(m.filterKeys(x => Pattern.keys.contains(x)))
    val returns = m.get("return").collect { case m: Map[String, String] => m }
    val subRules = m.get("rules").collect { case s: Seq[Map[String, _]] => s.map(Rule.apply) }
    Rule(pattern, returns, subRules)
  }

  def apply(s: Seq[Map[String, _]]): Rule =
    Rule(Pattern(Map.empty), None, Some(s.map(Rule.apply)))
}
