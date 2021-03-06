# urule-scala

Extract attributes from URL according to custom rule.

## Installation

### SBT

```scala
resolvers += "Urule Scala Maven Repository" at "https://yubessy.github.io/urule-scala/mvn/"

libraryDependencies += "com.github.yubessy" %% "urule" % "0.0.3"
```

## Usage

### Quickstart

Write your own rule in JSON, YAML or any other format that can be parsed into JSON-like structured object:

```yaml
- host: '^example\.com$'
  attrs:
    host: example.com
  rules:
    - path: '^/foo'
      attrs:
        path: foo

    - path: '^/bar'
      attrs:
        path: bar

- host: '^another\.example\.com$'
  attrs:
    host: example.com
    subdomain: another
```

Build rule and apply it to URL:

```scala
// Prepare parser on your need
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.scala.DefaultScalaModule
val mapper = new ObjectMapper(new YAMLFactory())
mapper.registerModule(DefaultScalaModule)

// Build rule
import com.github.yubessy.urule.Rule
val text = Source.fromFile("example.yaml").mkString
val raw = mapper.readValue(text, classOf[Seq[Map[String, _]]])
val rule = Rule(raw)

// Apply rule to URL and get values

rule.applyTo("http://example.com/foo")
// => Some(Result(None,Map(host -> example.com, path -> foo)))

rule.applyTo("http://another.example.com/")
// => Some(Result(None,Map(host -> example.com, subdomain -> another)))

rule.applyTo("http://other.com/")
// => None
```

### Detail

No docs are available currently. See [test cases](./src/test/resources/cases.yaml) for detailed usage.

## Build and Publish

### Use Github Pages as Maven Repository

```
$ git checkout master
$ sbt publish
$ git checkout gh-pages
$ git add mvn/
$ git commit -m "vx.x.x"
$ git push origin gh-pages
```
