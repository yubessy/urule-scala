package com.github.yubessy.urule

class Extractor(
  category: Option[String],
  attrs: Map[String, String]
) {
  def extract(m: MatchResult): Result =
    Result(extractCategory(m), extractAttrs(m))

  private def extractCategory(m: MatchResult): Option[String] =
    category.flatMap(exp => m.get(exp))

  private def extractAttrs(m: MatchResult): Map[String, String] =
    attrs.mapValues(exp => m.get(exp)).filter(_._2.nonEmpty).mapValues(_.get)
}

object Extractor {
  val keys = Seq("category", "attrs")

  def apply(e: RawElem): Extractor = {
    val category = e.get("category").collect { case s: String => s }
    val attrs = e.get("attrs").collect { case m: Map[String, String] => m }.getOrElse(Map.empty)
    new Extractor(category, attrs)
  }
}
