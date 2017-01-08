package com.github.yubessy

package object urule {
  type RuleElem = Map[String, _]
  type PatternElem = Map[String, _]
  type PatternParamsElem = Map[String, String]
  type AttributesElem = Map[String, String]

  type Attributes = Map[String, String]

  val emptyAttributes = Map.empty[String, String]
}
