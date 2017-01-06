package com.github.yubessy.urule.test

import com.github.yubessy.urule.Rule
import com.github.yubessy.urule.test.helper.YamlLoader
import com.github.yubessy.urule.types.RuleMap
import com.netaporter.uri.Uri.parse

class RuleSpec extends BaseSpec {
  describe("map type") {
    val ruleMap = YamlLoader.load("map_example").asInstanceOf[RuleMap]
    val rule = Rule(ruleMap)

    it("should match first rule") {
      val uri = parse("https://example.com/foo/")
      rule.extract(uri) should equal(Some("foo"))
    }

    it("should match second rule") {
      val uri = parse("https://example.com/bar/")
      rule.extract(uri) should equal(Some("bar"))
    }

    it("should match no rule") {
      val uri = parse("https://example.com/baz/")
      rule.extract(uri) should equal(None)
    }
  }

  describe("list type") {
    val ruleMap = YamlLoader.load("list_example").asInstanceOf[Seq[RuleMap]]
    val rule = Rule(ruleMap)

    it("should match first rule") {
      val uri = parse("https://example.com/")
      rule.extract(uri) should equal(Some("zero"))
    }

    it("should match second rule") {
      val uri = parse("https://subdomain.example.com/")
      rule.extract(uri) should equal(Some("one"))
    }

    it("should match third rule") {
      val uri = parse("https://subsub.domain.example.com/")
      rule.extract(uri) should equal(Some("two"))
    }
  }
}
