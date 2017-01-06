package com.github.yubessy.urule

import com.netaporter.uri.Uri

import scala.util.matching.Regex

case class Pattern(
  hostRegex: Option[Regex],
  pathRegex: Option[Regex],
  paramsRegex: Option[Map[String, Regex]]
) {
  def matchAll(uri: Uri): Boolean =
    matchHost(uri) && matchPath(uri) && matchParams(uri)

  private def matchRegex(target: String, regex: Regex): Boolean =
    regex.findFirstIn(target).nonEmpty

  private def matchHost(uri: Uri): Boolean =
    hostRegex.forall(re => uri.host.exists(h => matchRegex(h, re)))

  private def matchPath(uri: Uri): Boolean =
    pathRegex.forall(re => matchRegex(uri.path, re))

  private def matchParams(uri: Uri): Boolean =
    paramsRegex.forall(_.forall {
      case (k, re) => uri.query.param(k).exists(v => matchRegex(v, re))
    })
}

object Pattern {
  def apply(m: Map[String, _]): Pattern = {
    val hostRegex = m.get("host").collect { case s: String => s.r }
    val pathRegex = m.get("path").collect { case s: String => s.r }
    val paramsRegex = m.get("params").collect {
      case m: Map[String, String] => m.mapValues(_.r)
    }
    Pattern(hostRegex, pathRegex, paramsRegex)
  }
}
