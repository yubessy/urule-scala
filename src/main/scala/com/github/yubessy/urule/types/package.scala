package com.github.yubessy.urule

package object types {
  type StringAnyMap = Map[String, Any]
  type RuleMap = Map[String, StringAnyMap]
  type Action = Either[Seq[Rule], String]
}
