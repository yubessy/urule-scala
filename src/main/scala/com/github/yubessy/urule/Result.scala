package com.github.yubessy.urule

case class Result(
  category: Option[String] = None,
  attrs: Map[String, String] = Map.empty
) {
  def update(newer: Result) =
    copy(newer.category.orElse(category), attrs ++ newer.attrs)
}
