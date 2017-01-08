package com.github.yubessy.urule

import com.netaporter.uri.Uri

class Rule(
  pattern: Pattern,
  attributes: Attributes,
  children: Seq[Rule]
) {
  def extract(uri: Uri, previous: Attributes = emptyAttributes): Option[Attributes] =
    pattern.matchAll(uri).map(matchResult => {
      val current = extractByCurrent(uri, matchResult)
      val next = extractByChildren(uri)
      previous ++ current ++ next
    })

  private def extractByCurrent(uri: Uri, matchResult: MatchResult): Attributes =
    attributes.mapValues(exp => matchResult.get(exp)).filter(_._2.nonEmpty).mapValues(_.get)

  private def extractByChildren(uri: Uri): Attributes =
    children.view.map(_.extract(uri)).collectFirst { case Some(attrs) => attrs }.getOrElse(Map.empty)
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
