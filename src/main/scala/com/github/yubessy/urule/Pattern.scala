package com.github.yubessy.urule

import com.netaporter.uri.Uri

import scala.util.matching.Regex

case class Pattern(
  host: Option[Regex],
  path: Option[Regex]
) {
  def matchTo(uri: Uri): Boolean = {
    (
      host.forall(_.findFirstIn(uri.host.getOrElse("")).nonEmpty)
      && path.forall(_.findFirstIn(uri.path).nonEmpty)
    )
  }
}

object Pattern {
  def build(m: Map[String, String]): Pattern = Pattern(
    host = m.get("host").map(_.r),
    path = m.get("path").map(_.r)
  )
}
