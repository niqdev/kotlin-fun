package com.github.niqdev.json

import com.github.niqdev.json.core.NonEmptyList
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SchemaTest :
  WordSpec({

    val schemaJson = "/employee.schema.json".readFromResource()
    val schema = Schema.load(schemaJson).getOrNull()

    "Schema" should {

      "verify valid" {
        val example = "/employee.json".readFromResource()
        schema?.validate(example)?.isValid() shouldBe true
      }

      "verify invalid" {
        val example = "/employee-invalid.json".readFromResource()
        val expected =
          NonEmptyList.of(
            ValidationError("#/hobbies: required key [outdoor] not found"),
            ValidationError("#/age: 13 is not greater or equal to 16"),
          )

        val result = schema?.validate(example)
        result?.isValid() shouldBe false
        // TODO NonEmptyList matcher
        result?.getInvalidOrNull()?.toList() shouldBe expected.toList()
      }

      "verify compatibility: remove" {
        val latestJsonSchema = "/employee-remove.schema.json".readFromResource()
        val latestSchema = Schema.load(latestJsonSchema).getOrNull()

        latestSchema?.isBackwardCompatible(schemaJson)?.isValid() shouldBe true
        latestSchema?.isForwardCompatible(schemaJson)?.isValid() shouldBe false
        latestSchema?.isForwardCompatible(schemaJson)?.getInvalidOrNull()?.toList() shouldBe
          NonEmptyList
            .of(
              CompatibilityError(
                "Found incompatible change: Difference{jsonPath='#/properties/hobbies/properties/outdoor', type=PROPERTY_ADDED_TO_OPEN_CONTENT_MODEL}",
              ),
            ).toList()
      }

      "verify compatibility: rename" {
        val latestJsonSchema = "/employee-rename.schema.json".readFromResource()
        val latestSchema = Schema.load(latestJsonSchema).getOrNull()

        latestSchema?.isBackwardCompatible(schemaJson)?.isValid() shouldBe false
        latestSchema?.isBackwardCompatible(schemaJson)?.getInvalidOrNull()?.toList() shouldBe
          NonEmptyList
            .of(
              CompatibilityError(
                "Found incompatible change: Difference{jsonPath='#/properties/name', type=PROPERTY_REMOVED_FROM_CLOSED_CONTENT_MODEL}",
              ),
            ).toList()

        latestSchema?.isForwardCompatible(schemaJson)?.isValid() shouldBe false
        latestSchema?.isForwardCompatible(schemaJson)?.getInvalidOrNull()?.toList() shouldBe
          NonEmptyList
            .of(
              CompatibilityError(
                "Found incompatible change: Difference{jsonPath='#/properties/ops', type=PROPERTY_REMOVED_FROM_CLOSED_CONTENT_MODEL}",
              ),
              CompatibilityError(
                "Found incompatible change: Difference{jsonPath='#/properties/name', type=REQUIRED_PROPERTY_ADDED_TO_UNOPEN_CONTENT_MODEL}",
              ),
            ).toList()
      }
    }
  })
