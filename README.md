# urule-scala

Extract attributes from URL according to custom rule.

## Usage

Write your own rule in JSON, YAML or any other format that can be parsed to into map and list of strings:

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

- host: '^another.example\.com$'
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
