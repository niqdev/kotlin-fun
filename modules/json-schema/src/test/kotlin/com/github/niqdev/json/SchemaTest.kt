package com.github.niqdev.json

import com.github.niqdev.json.core.NonEmptyList
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SchemaTest : WordSpec({

  val schemaJson = "/employee.schema.json".jsonToString()
  val schema = Schema.load(schemaJson).getOrNull()

  "Schema" should {

    "verify valid" {
      val example = "/employee.json".jsonToString()
      schema?.validate(example)?.isValid() shouldBe true
    }

    "verify invalid" {
      val example = "/employee-invalid.json".jsonToString()
      val expected = NonEmptyList.of(
        ValidationError("#/hobbies: required key [outdoor] not found"),
        ValidationError("#/age: 13 is not greater or equal to 16")
      )

      val result = schema?.validate(example)
      result?.isValid() shouldBe false
      // TODO NonEmptyList matcher
      result?.getInvalidOrNull()?.toList() shouldBe expected.toList()
    }
  }
})
