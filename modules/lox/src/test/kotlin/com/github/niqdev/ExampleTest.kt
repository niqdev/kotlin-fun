package com.github.niqdev

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ExampleTest : WordSpec({

  "Example" should {
    "readFile" {
      Lox.readFile("data/example.lox").isNotBlank() shouldBe true
    }
  }
})
