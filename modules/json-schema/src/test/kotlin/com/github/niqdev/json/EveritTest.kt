package com.github.niqdev.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import org.everit.json.schema.Schema as EveritSchema

// https://github.com/everit-org/json-schema
// https://github.com/circe/circe-json-schema
class EveritTest : WordSpec({

  "Everit" should {
    "validate" {
      // https://www.mongodb.com/basics/json-schema-examples
      val schema = "/employee.schema.json".jsonToString()
      val example = "/employee.json".jsonToString()

      val everitSchema = Schema.loadFromString(schema)

      val exception = shouldThrow<ValidationException> {
        everitSchema.validate(JSONObject(example))
      }
      exception.allMessages shouldBe listOf("#/hobbies: required key [outdoor] not found", "#/age: 13 is not greater or equal to 16")
    }
  }
})

private object Schema {
  // TODO Try/Either
  fun loadFromString(value: String): EveritSchema =
    SchemaLoader.builder()
      .schemaJson(JSONTokener(value).nextValue())
      .draftV7Support()
      .build()
      .load()
      .build()
}

// TODO Try/Either
private fun String.jsonToString(): String =
  object {}.javaClass.getResource(this)?.readText() ?: "INVALID_PATH"
