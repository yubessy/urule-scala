package com.github.yubessy.urule

import com.netaporter.uri.Uri

import scala.util.matching.Regex

case class Pattern(
  host: Option[Regex],
  path: Option[Regex]
) {
  def matchAll(uri: Uri): Boolean =
    matchHost(uri) && matchPath(uri)

  private def matchHost(uri: Uri): Boolean =
    host.forall(_.findFirstIn(uri.host.getOrElse("")).nonEmpty)

  private def matchPath(uri: Uri): Boolean =
    path.forall(_.findFirstIn(uri.path).nonEmpty)
}

object Pattern {
  def apply(m: Map[String, String]): Pattern = Pattern(
    host = m.get("host").map(_.r),
    path = m.get("path").map(_.r)
  )

  def any = Pattern(Map("host" -> ".*"))
}
