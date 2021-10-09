package com.github.niqdev

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class LoxTest : WordSpec({

  "Lox" should {
    "readFile" {
      Lox.readFile("data/example1.lox").isNotBlank() shouldBe true
    }
  }
})
