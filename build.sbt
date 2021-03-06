name := "urule"

organization := "com.github.yubessy"

version := "0.0.4"

scalaVersion := "2.11.8"

crossScalaVersions := Seq(
  "2.10.6",
  "2.11.8",
  "2.12.3"
)

libraryDependencies ++= Seq(
  "com.netaporter" %% "scala-uri" % "0.4.16",

  "com.fasterxml.jackson.core" % "jackson-core" % "2.8.4" % "test",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.4" % "test",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.8.4" % "test",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

publishTo := Some(
  Resolver.file(
    "gh-pages", file("mvn")
  )(
    Patterns(true, Resolver.mavenStyleBasePattern)
  )
)
