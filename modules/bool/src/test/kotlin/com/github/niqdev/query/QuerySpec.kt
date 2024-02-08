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

class QuerySpec : WordSpec({

  "Query" should {

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
