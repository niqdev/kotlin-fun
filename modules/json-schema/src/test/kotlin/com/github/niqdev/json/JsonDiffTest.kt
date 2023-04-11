package com.github.niqdev.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.flipkart.zjsonpatch.JsonDiff
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

// property "value" contains more information
private data class Patch(val op: String, val path: String)
private data class ExampleJson(val string: String, val boolean: Boolean, val ints: List<Int>)

class JsonDiffTest : WordSpec({

  val mapper = jacksonObjectMapper()
    .configure(SerializationFeature.INDENT_OUTPUT, false)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  "JsonDiff" should {

    "verify json patch" {
      val sourceValue = ExampleJson("myString", false, listOf(1, 2, 3))
      val jsonTarget = """{"string":"myValue","boolean":false,"ints":[42]}"""
      // see readValue and writeValueAsString
      val source = mapper.valueToTree<JsonNode>(sourceValue)
      val target = mapper.readTree(jsonTarget)

      val rawPatch = JsonDiff.asJson(source, target)
      val patch = mapper.treeToValue<List<Patch>>(rawPatch)

      patch.size shouldBe 4
    }
  }
})
