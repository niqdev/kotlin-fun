package com.github.niqdev.todo

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

// TODO https://github.com/persvr/rql

// TODO fixpoint
// https://www.youtube.com/watch?v=7xSfLPD6tiQ
// https://www.youtube.com/watch?v=oRLkb6mqvVM
// TODO https://arosien.github.io/talks/free-boolean-algebras.html

// TODO lenses https://arrow-kt.io/learn/immutable-data/intro

// Encoder[A]: A -> Json
// Decoder[A]: Json -> A

// parser combinator
// https://github.com/NickLydon/KotZen/blob/main/src/test/kotlin/parsingJsonExample/JsonParser.kt

// profunctor
// https://github.com/uberto/kondor-json
// https://github.com/uberto/kotlin-pearls/blob/master/src/main/kotlin/com/ubertob/adjunction/Profunctor.kt

class QuerySpec : WordSpec({

  "Query" should {

    "parse query" {
      val expected = FilterPredicate.In(
        FilterPredicate.Filter(name = "foo", value = "3"),
        FilterPredicate.Filter(name = "bar", value = "42")
      )
      val result = FilterParser.query("foo=3&bar=42")
      result.isSuccess() shouldBe true
      result.getOrNull() shouldBe expected
    }

    "parse json filter" {
      val expected = FilterPredicate.Filter(name = "foo", value = "3")
      val value = """{"name":"foo","value":"3"}"""
      val result = FilterParser.json(value)
      result.isSuccess() shouldBe true
      result.getOrNull() shouldBe expected
    }

    "parse json filters" {
      val expected = FilterPredicate.In(
        FilterPredicate.Filter(name = "foo", value = "3"),
        FilterPredicate.Filter(name = "bar", value = "42")
      )
      // TODO ideally "[{"name":"foo","value":"3"},{"name":"bar","value":"42"}]"
      val value = """{"filters":[{"name":"foo","value":"3"},{"name":"bar","value":"42"}]}"""
      val result = FilterParser.json(value)
      result.isSuccess() shouldBe true
      result.getOrNull() shouldBe expected
    }

    "simple" {
      // https://dundalek.com/rql
      val rql = "and(eq(foo,3),eq(foo,bar))"
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
