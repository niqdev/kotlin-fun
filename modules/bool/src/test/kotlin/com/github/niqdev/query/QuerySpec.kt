package com.github.niqdev.query

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

// TODO https://github.com/persvr/rql
// TODO https://github.com/anchore/syft
// syft niqdev/kotlin-fun-http -o cyclonedx-json > foo.json
// TODO https://falco.org/docs/rules/basic-elements
// TODO https://www.openpolicyagent.org/docs/latest/#rego
// TODO fixpoint
// https://www.youtube.com/watch?v=7xSfLPD6tiQ
// https://www.youtube.com/watch?v=oRLkb6mqvVM
// TODO https://arosien.github.io/talks/free-boolean-algebras.html

// TODO lenses https://arrow-kt.io/learn/immutable-data/intro

// Encoder[A]: A -> Json
// Decoder[A]: Json -> A

// QueryParser "foo=3&bar=42"
// JsonParser "[{"name":"foo","value":"3"},{"name":"bar","value":"42"}]"

class QuerySpec : WordSpec({

  "Query" should {

    "parser" {
      val expected = FilterPredicate.In(
        head = FilterPredicate.Filter(name = "foo", value = "3"),
        tail = listOf(FilterPredicate.Filter(name = "bar", value = "42"))
      )
      val result = QueryParser.parse("foo=3&bar=42")
      result.isSuccess() shouldBe true
      result.getOrNull() shouldBe expected
    }

    "simple" {
      // https://dundalek.com/rql
      val rql = "and(eq(foo,3),eq(foo,bar))"
      val query = "foo=3&bar=42"
      val json = """
        {
          "query": {
            "and": [
              { "name": "color", "operator": "eq", "value": "red" },
              { "name": "size", "operator": "gt", "value": "42" },
              "or": [
                { "name": "brand", "operator": "neq", "value": "nike" }
              ]
            ]
          }
        }
      """.trimIndent()
      // json shouldBe "(color == red && size > 42) || brand != nike"
      "foo" shouldBe "foo"
    }
  }
})
