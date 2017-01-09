package com.github.yubessy.urule

import com.netaporter.uri.Uri

import scala.util.matching.Regex

class Matcher(
  host: Option[Regex],
  path: Option[Regex],
  params: Option[Map[String, Regex]]
) {
  def matchAll(uri: Uri): Option[MatchResult] =
    for {
      hostResult <- matchHostIfNecessary(uri)
      pathResult <- matchPathIfNecessary(uri)
      paramsResult <- matchParamsIfNecessary(uri)
    } yield new MatchResult(hostResult, pathResult, paramsResult)

  private def matchHostIfNecessary(uri: Uri): Option[Option[Regex.Match]] =
    host match {
      case Some(re) => uri.host.flatMap(h => re.findFirstMatchIn(h).map(x => Some(x)))
      case None => Some(None)
    }

  private def matchPathIfNecessary(uri: Uri): Option[Option[Regex.Match]] =
    path match {
      case Some(re) => re.findFirstMatchIn(uri.path).map(x => Some(x))
      case None => Some(None)
    }

  private def matchParamsIfNecessary(uri: Uri): Option[Map[String, Regex.Match]] =
    params match {
      case Some(res) => {
        val matches = res.map {
          case (k, re) => (k, uri.query.param(k).flatMap(v => re.findFirstMatchIn(v)))
        }
        if (matches.forall(_._2.nonEmpty)) Some(matches.mapValues(_.get)) else None
      }
      case None => Some(Map.empty)
    }
}

object Matcher {
  val keys = Seq("host", "path", "params")

  def apply(e: RawElem): Matcher = {
    val hostRegex = e.get("host").collect { case s: String => s.r }
    val pathRegex = e.get("path").collect { case s: String => s.r }
    val paramsRegex = e.get("params").collect { case m: Map[String, String] => m.mapValues(_.r) }
    new Matcher(hostRegex, pathRegex, paramsRegex)
  }
}
