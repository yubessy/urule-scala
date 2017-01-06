package com.github.yubessy.urule

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
  def build(m: Map[String, Any]): Pattern = {
    val hostRegex = m.getOrElse("host", anyString) match {
      case s: String => s.r
      case null => anyString.r
      case _ => throw new Exception("host is not String")
    }
    val pathRegex = m.getOrElse("path", anyString) match {
      case s: String => s.r
      case null => anyString.r
      case _ => throw new Exception("path is not String")
    }
    Pattern(
      hostRegex = hostRegex,
      pathRegex = pathRegex,
      paramsRegex = Map.empty
    )
  }

  val any = build(Map.empty)

  private val anyString = ".*"
}
