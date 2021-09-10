package com.github.niqdev

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ExampleTest : WordSpec({

  "Example" should {
    "readFile" {
      println("Current working directory: ${System.getProperty("user.dir")}")

      Lox.readFile("data/example.lox").isNotBlank() shouldBe true
    }
  }
})
