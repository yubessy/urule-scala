package com.github.yubessy.urule.test

import com.github.yubessy.urule._
import com.github.yubessy.urule.test.helper.YamlLoader
import org.scalatest._

class Spec extends FunSpec with Matchers {
  describe("test cases") {
    val cases = YamlLoader.load("cases").asInstanceOf[Seq[Map[String, _]]]
    cases.foreach(m => {
      val rule = m("rule") match {
        case e: RawElem => Rule(e)
        case s: Seq[RawElem] => Rule(s)
      }

      val caption = m("caption").asInstanceOf[String]
      describe(caption) {
        m("cases").asInstanceOf[Seq[Map[String, _]]].foreach(c => {
          val expected = if (c.keys.exists(k => Seq("category", "attrs").contains(k))) {
            Some(Result(
              category = c.get("category").asInstanceOf[Option[String]],
              attrs = c.get("attrs").map(_.asInstanceOf[Map[String, String]]).getOrElse(Map.empty)
            ))
          } else {
            None
          }

          val url = c("url").asInstanceOf[String]
          it(url) { rule.applyTo(url) should equal(expected) }
        })
      }
    })
  }
}
