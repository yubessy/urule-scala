package com.github.yubessy.urule

import com.github.yubessy.urule.types.StringAnyMap
import com.netaporter.uri.Uri

import scala.util.matching.Regex

case class Pattern(
  hostRegex: Regex,
  pathRegex: Regex,
  paramsRegex: Map[String, Regex]
) {
  def matchAll(uri: Uri): Boolean =
    matchHost(uri) && matchPath(uri) && matchParams(uri)

  private def matchRegex(target: String, regex: Regex): Boolean =
    regex.findFirstIn(target).nonEmpty

  private def matchHost(uri: Uri): Boolean =
    uri.host.exists(host => matchRegex(host, hostRegex))

  private def matchPath(uri: Uri): Boolean =
    matchRegex(uri.path, pathRegex)

  private def matchParams(uri: Uri): Boolean =
    paramsRegex.forall {
      case (key, re) => uri.query.param(key).exists(value => matchRegex(value, re))
    }
}

object Pattern {
  def apply(m: StringAnyMap): Pattern = {
    val hostRegex = m.get("host") match {
      case Some(s: String) => s.r
      case _ => anyString
    }
    val pathRegex = m.get("path") match {
      case Some(s: String) => s.r
      case _ => anyString
    }
    val paramsRegex = m.get("params") match {
      case Some(m: Map[String, String]) => m.mapValues(_.r)
      case _ => Map.empty[String, Regex]
    }
    Pattern(
      hostRegex = hostRegex,
      pathRegex = pathRegex,
      paramsRegex = paramsRegex
    )
  }

  private def anyString = ".*".r
}
