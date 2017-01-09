package com.github.yubessy.urule

case class Result(
  attrs: Map[String, String]
) {
  def update(newer: Result) =
    copy(attrs ++ newer.attrs)
}
