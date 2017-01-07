package com.github.yubessy.urule.test

import com.github.yubessy.urule.Rule
import com.github.yubessy.urule.test.helper.YamlLoader
import com.netaporter.uri.Uri.parse
import org.scalatest._

class IntegrationSpec extends FunSpec with Matchers {
  describe("map type") {
    val ruleMap = YamlLoader.load("map_example").asInstanceOf[Map[String, _]]
    val rule = Rule(ruleMap)

    it("should match first rule") {
      val uri = parse("https://example.com/foo/")
      rule.extract(uri) should equal(Some(Map("tag" -> "foo")))
    }

    it("should match second rule") {
      val uri = parse("https://example.com/?bar=xxx")
      rule.extract(uri) should equal(Some(Map("tag" -> "bar")))
    }

    it("should match no rule") {
      val uri = parse("https://example.com/baz/")
      rule.extract(uri) should equal(None)
    }
  }

  describe("list type") {
    val ruleMap = YamlLoader.load("list_example").asInstanceOf[Seq[Map[String, _]]]
    val rule = Rule(ruleMap)

    it("should match first rule") {
      val uri = parse("https://example.com/")
      rule.extract(uri) should equal(Some(Map("tag" -> "zero")))
    }

    it("should match second rule") {
      val uri = parse("https://subdomain.example.com/")
      rule.extract(uri) should equal(Some(Map("tag" -> "one")))
    }

    it("should match third rule") {
      val uri = parse("https://subsub.domain.example.com/")
      rule.extract(uri) should equal(Some(Map("tag" -> "two")))
    }
  }
}
