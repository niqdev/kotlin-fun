package com.github.niqdev.json

import io.confluent.kafka.schemaregistry.CompatibilityLevel
import io.confluent.kafka.schemaregistry.json.JsonSchema
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SchemaRegistryCompatibilityTest : WordSpec({

  "SchemaRegistryCompatibility" should {

    "verify removed field" {
      val previousSchema = "/employee.schema.json".readFromResource()
      val latestSchema = "/employee-remove.schema.json".readFromResource()

      // previousSchemas – full schema history in chronological order
      JsonSchema(latestSchema).isCompatible(CompatibilityLevel.BACKWARD, listOf(JsonSchema(previousSchema))) shouldBe emptyList()
      JsonSchema(latestSchema).isCompatible(CompatibilityLevel.FORWARD, listOf(JsonSchema(previousSchema))) shouldBe listOf(
        "Found incompatible change: Difference{jsonPath='#/properties/hobbies/properties/outdoor', type=PROPERTY_ADDED_TO_OPEN_CONTENT_MODEL}"
      )
    }

    "verify renamed field" {
      val previousSchema = "/employee.schema.json".readFromResource()
      val latestSchema = "/employee-rename.schema.json".readFromResource()

      JsonSchema(latestSchema).isCompatible(CompatibilityLevel.BACKWARD, listOf(JsonSchema(previousSchema))) shouldBe listOf(
        "Found incompatible change: Difference{jsonPath='#/properties/name', type=PROPERTY_REMOVED_FROM_CLOSED_CONTENT_MODEL}"
      )
      JsonSchema(latestSchema).isCompatible(CompatibilityLevel.FORWARD, listOf(JsonSchema(previousSchema))) shouldBe listOf(
        "Found incompatible change: Difference{jsonPath='#/properties/ops', type=PROPERTY_REMOVED_FROM_CLOSED_CONTENT_MODEL}",
        "Found incompatible change: Difference{jsonPath='#/properties/name', type=REQUIRED_PROPERTY_ADDED_TO_UNOPEN_CONTENT_MODEL}"
      )
    }
  }
})
