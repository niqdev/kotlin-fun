package com.github.niqdev

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class AppTest : WordSpec({

  "App" should {
    "greeting" {
      App().greeting shouldBe "Hello World!"
    }
  }
})
