package com.github.yubessy.urule

class Extractor(
  category: Option[String],
  attrs: Map[String, String]
) {
  def extract(m: MatchResult): Result =
    Result(
      category = category.flatMap(m.get),
      attrs = attrs.mapValues(m.get).filter(_._2.nonEmpty).mapValues(_.get)
    )
}

object Extractor {
  val keys = Seq("category", "attrs")

  def apply(e: RawElem): Extractor =
    new Extractor(
      category = e.get("category").collect { case s: String => s },
      attrs = e.get("attrs").collect { case m: Map[String, String] => m }.getOrElse(Map.empty)
    )
}
