package com.github.yubessy.urule

import com.netaporter.uri.Uri

class Rule(
  matcher: Matcher,
  extractor: Extractor,
  children: Seq[Rule]
) {
  def applyTo(uri: String): Option[Result] =
    applyTo(Uri.parse(uri))

  private def applyTo(uri: Uri, parent: Option[Result] = None): Option[Result] =
    matcher.matchAll(uri).map(matchResult => {
      val current = extractor.extract(matchResult)
      val updated = parent.map(_.update(current)).getOrElse(current)
      val child = applyToChildren(uri)
      child.map(n => updated.update(n)).getOrElse(current)
    })

  private def applyToChildren(uri: Uri): Option[Result] =
    children.view.map(_.applyTo(uri)).collectFirst { case Some(r) => r }
}

object Rule {
  def apply(e: RawElem): Rule = {
    val matcher = Matcher(e.filterKeys(k => Matcher.keys.contains(k)))
    val extractor = Extractor(e.filterKeys(k => Extractor.keys.contains(k)))
    val children = makeChildren(e)
    new Rule(matcher, extractor, children)
  }

  def apply(s: Seq[RawElem]): Rule =
    new Rule(Matcher(Map.empty), Extractor(Map.empty), s.map(Rule.apply))

  private def makeChildren(e: RawElem): Seq[Rule] =
    e.get("rules").collect {
      case s: Seq[RawElem] => s.map(Rule.apply)
    }.getOrElse(Seq.empty)
}
