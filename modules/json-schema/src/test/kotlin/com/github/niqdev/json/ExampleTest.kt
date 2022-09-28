package com.github.niqdev.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import net.pwall.json.schema.JSONSchema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import org.everit.json.schema.Schema as EveritSchema

class ExampleTest : WordSpec({

  fun loadFromString(value: String): EveritSchema =
    SchemaLoader.builder()
      .schemaJson(JSONTokener(value).nextValue())
      .draftV7Support()
      .build()
      .load()
      .build()

  "Example" should {

    // https://github.com/everit-org/json-schema
    // https://github.com/circe/circe-json-schema
    "validate net.pwall" {
      // https://www.mongodb.com/basics/json-schema-examples
      val schema = "/employee.schema.json".readFromResource()
      val example = "/employee-invalid.json".readFromResource()

      val jsonSchema = JSONSchema.parse(schema)
      jsonSchema.validateBasic(example).errors?.forEach {
        println("${it.keywordLocation} | ${it.absoluteKeywordLocation} | ${it.instanceLocation} | ${it.error}")
      }
      jsonSchema.validate(example) shouldBe false
    }

    // https://github.com/pwall567/json-kotlin-schema
    "validate org.everit" {
      val schema = "/employee.schema.json".readFromResource()
      val example = "/employee-invalid.json".readFromResource()

      val everitSchema = loadFromString(schema)

      val exception = shouldThrow<ValidationException> {
        everitSchema.validate(JSONObject(example))
      }
      exception.allMessages shouldBe listOf(
        "#/hobbies: required key [outdoor] not found",
        "#/age: 13 is not greater or equal to 16"
      )
    }
  }
})
