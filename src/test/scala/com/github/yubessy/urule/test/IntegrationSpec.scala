package com.github.yubessy.urule.test

import com.github.yubessy.urule._
import com.github.yubessy.urule.test.helper.YamlLoader
import com.netaporter.uri.Uri.parse
import org.scalatest._

class IntegrationSpec extends FunSpec with Matchers {
  describe("map type") {
    val raw = YamlLoader.load("map_example").asInstanceOf[Map[String, _]]
    val rule = Rule(raw)

    it("should match first rule") {
      val target = parse("https://example.com/foo/")
      val expected = Result(Map("host" -> "any", "tag" -> "foo"))
      rule.applyTo(target) should contain(expected)
    }

    it("should match second rule") {
      val target = parse("https://example.com/?bar=xxx")
      val expected = Result(Map("host" -> "any", "tag" -> "bar"))
      rule.applyTo(target) should contain(expected)
    }

    it("should match no rule") {
      val target = parse("https://example.com/baz/")
      val expected = Result(Map("host" -> "any"))
      rule.applyTo(target) should contain(expected)
    }
  }

  describe("list type") {
    val raw = YamlLoader.load("list_example").asInstanceOf[Seq[Map[String, _]]]
    val rule = Rule(raw)

    it("should match first rule") {
      val target = parse("https://example.com/")
      val expected = Result(Map("tag" -> "zero"))
      rule.applyTo(target) should contain(expected)
    }

    it("should match second rule") {
      val target = parse("https://subdomain.example.com/")
      val expected = Result(Map("tag" -> "one"))
      rule.applyTo(target) should contain(expected)
    }

    it("should match third rule") {
      val target = parse("https://subsub.domain.example.com/")
      val expected = Result(Map("tag" -> "two"))
      rule.applyTo(target) should contain(expected)
    }
  }
}
