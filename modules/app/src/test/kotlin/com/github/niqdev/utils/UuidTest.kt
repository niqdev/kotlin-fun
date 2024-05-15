package com.github.niqdev.utils

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class UuidTest : WordSpec({

  "UuidUtils" should {
    "generate id" {
      MyModel.toId("foo") shouldBe UUID.fromString("b2b26804-dd8a-5e4e-84d7-b66ba06668d0")
    }
  }
})
