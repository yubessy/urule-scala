package com.github.yubessy.urule.test.helper

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import scala.io.Source

object YamlLoader {
  val mapper = new ObjectMapper(new YAMLFactory())
  mapper.registerModule(DefaultScalaModule)

  def load(name: String): Any = {
    val yaml = Source.fromURL(getClass.getResource(f"/$name.yaml")).mkString
    mapper.readValue(yaml, classOf[Any])
  }
}
