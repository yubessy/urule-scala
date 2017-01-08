package com.github.yubessy.urule

import com.netaporter.uri.Uri

class Rule(
  pattern: Pattern,
  attributes: Attributes,
  children: Seq[Rule]
) {
  def extract(uri: Uri, tmp: Attributes = emptyAttributes): Option[Attributes] = {
    val matchResult = pattern.matchAll(uri)
    if (matchResult) {
      val updated = tmp ++ attributes
      val childResults = extractByFirst(uri, children)
      childResults.map(updated ++ _).orElse(Some(updated))
    } else {
      None
    }
  }

  private def extractByFirst(uri: Uri, rules: Seq[Rule]): Option[Attributes] =
    rules.view.map(_.extract(uri)).collectFirst { case Some(attributes) => attributes }
}

object Rule {
  def apply(m: RuleElem): Rule =
    new Rule(makePattern(m), makeAttributes(m), makeChildren(m))

  def apply(s: Seq[RuleElem]): Rule =
    new Rule(Pattern(Map.empty), Map.empty, s.map(Rule.apply))

  private def makePattern(e: RuleElem): Pattern =
    Pattern(e.filterKeys(k => Pattern.keys.contains(k)))

  private def makeAttributes(e: RuleElem): Attributes =
    e.get("attrs").collect {
      case a: AttributesElem => a.asInstanceOf[Attributes]
    }.getOrElse(emptyAttributes)

  private def makeChildren(e: RuleElem): Seq[Rule] =
    e.get("rules").collect {
      case s: Seq[RuleElem] => s.map(Rule.apply)
    }.getOrElse(Seq.empty)
}
