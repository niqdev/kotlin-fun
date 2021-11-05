package com.github.niqdev.kondor

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class PersonTest : WordSpec({

  "Person" should {
    "verify" {
      val person = Person("hello")
      val json = JPerson.toJson(person)

      json shouldBe """{"name": "hello"}"""
      JPerson.fromJson(json).orThrow() shouldBe person
    }
  }
})
