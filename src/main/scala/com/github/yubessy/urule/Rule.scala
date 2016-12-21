package com.github.yubessy.urule

import com.netaporter.uri.Uri

case class Rule(
  pattern: Pattern,
  action: Either[Seq[Rule], String]
) {
  def applyTo(uri: Uri): Option[String] = {
    if (pattern.matchTo(uri)) action match {
      case Right(s) => Some(s)
      case Left(rules) => rules.view.map(_.applyTo(uri)).collectFirst{
        case Some(s) => s
      }
    } else {
      None
    }
  }
}

object Rule {
  def build(m: Any): Rule = {
    m match {
      case m: Map[String, Map[String, Any]] => {
        val pattern = m("pattern") match {
          case p: Map[String, String] => Pattern.build(p)
        }
        val action = if (m("action").contains("rules")) {
          m("action")("rules") match {
            case s: Seq[Any] => Left(s.map(Rule.build))
          }
        } else {
          m("action")("return") match {
            case s: String => Right(s)
          }
        }
        Rule(pattern, action)
      }
    }
  }
}
