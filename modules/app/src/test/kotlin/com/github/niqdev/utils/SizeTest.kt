package com.github.niqdev.utils

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class SizeTest : WordSpec({

  "Size extensions" should {
    "verify format" {
      val jsonString = """"{"key":"value","array":[1,2,3]}""""
      jsonString.sizeInBytes() shouldBe 33
      jsonString.humanReadableSize() shouldBe "1 B"
    }
  }
})
