package com.github.yubessy.urule.test

import com.github.yubessy.urule._
import com.github.yubessy.urule.test.helper.YamlLoader
import org.scalatest._

class Spec extends FunSpec with Matchers {
  def getRule(raw: Any): Rule =
    raw match {
      case e: RawElem => Rule(e)
      case s: Seq[RawElem] => Rule(s)
    }

  def getExpected(raw: RawElem): Option[Result] =
    if (raw.keys.exists(Extractor.keys.contains)) {
      Some(Result(
        category = raw.get("category").map(_.asInstanceOf[String]),
        attrs = raw.get("attrs").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty)
      ))
    } else {
      None
    }

  describe("test cases") {
    val cases = YamlLoader.load("cases").asInstanceOf[Seq[Map[String, _]]]
    cases.foreach(m => {
      val caption = m("caption").asInstanceOf[String]
      val rule = getRule(m("rule"))
      describe(caption) {
        m("cases").asInstanceOf[Seq[RawElem]].foreach(c => {
          val url = c("url").asInstanceOf[String]
          val expected = getExpected(c)
          it(url) { rule.applyTo(url) should equal(expected) }
        })
      }
    })
  }
}
