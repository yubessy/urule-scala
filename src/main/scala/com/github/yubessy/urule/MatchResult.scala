package com.github.yubessy.urule

import scala.util.Try
import scala.util.matching.Regex

class MatchResult(
  host: Option[Regex.Match],
  path: Option[Regex.Match],
  params: Map[String, Regex.Match]
) {
  def get(exp: String): Option[String] =
    if (exp.startsWith("$")) eval(exp) else Some(exp)

  private def eval(query: String): Option[String] =
    query match {
      case q("host", group, null) => host.flatMap(_.get(group))
      case q("path", group, null) => path.flatMap(_.get(group))
      case q("params", key, group) => params.get(key).flatMap(_.get(group))
      case _ => None
    }

  private val q = """^\$(host|path|params)(?:\.(\w+))(?:\.(\w+))?$""".r

  implicit private class WrappedRegexMatch(m: Regex.Match) {
    def get(g: String): Option[String] =
      (for (i <- Try(g.toInt)) yield Try(m.group(i))).getOrElse(Try(m.group(g))).toOption
  }
}
