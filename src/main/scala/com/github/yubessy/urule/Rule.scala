package com.github.yubessy.urule

import com.netaporter.uri.Uri

class Rule(
  matcher: Matcher,
  extractor: Extractor,
  children: Seq[Rule]
) {
  def applyTo(uri: String): Option[Result] =
    applyTo(Uri.parse(uri), None).flatMap(x => if (x.isEmpty) None else Some(x))

  private def applyTo(uri: Uri, parent: Option[Result]): Option[Result] =
    matcher.matchAll(uri).map(matchResult => {
      val current = extractor.extract(matchResult)
      val updated = parent.map(_.update(current)).getOrElse(current)
      val child = applyToChildren(uri)
      child.map(n => updated.update(n)).getOrElse(current)
    })

  private def applyToChildren(uri: Uri): Option[Result] =
    children.view.map(_.applyTo(uri, None)).collectFirst { case Some(r) => r }
}

object Rule {
  def apply(e: RawElem): Rule =
    new Rule(
      matcher = Matcher(e.filterKeys(Matcher.keys.contains)),
      extractor = Extractor(e.filterKeys(Extractor.keys.contains)),
      children = makeChildren(e)
    )

  def apply(s: Seq[RawElem]): Rule =
    new Rule(
      matcher = Matcher(Map.empty),
      extractor = Extractor(Map.empty),
      children = s.map(Rule.apply)
    )

  private def makeChildren(e: RawElem): Seq[Rule] =
    e.get("rules") match {
      case Some(s: Seq[RawElem]) => s.map(Rule.apply)
      case _ => Seq.empty
    }
}
