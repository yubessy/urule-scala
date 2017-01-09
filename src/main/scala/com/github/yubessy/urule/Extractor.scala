package com.github.yubessy.urule

class Extractor(
  attrs: Map[String, String]
) {
  def extract(m: MatchResult): Result =
    Result(extractAttrs(m))

  private def extractAttrs(m: MatchResult): Map[String, String] =
    attrs.mapValues(exp => m.get(exp)).filter(_._2.nonEmpty).mapValues(_.get)
}

object Extractor {
  val keys = Seq("attrs")

  def apply(e: RawElem): Extractor = {
    val attrs = e.get("attrs").collect { case m: Map[String, String] => m }.getOrElse(Map.empty)
    new Extractor(attrs)
  }
}
