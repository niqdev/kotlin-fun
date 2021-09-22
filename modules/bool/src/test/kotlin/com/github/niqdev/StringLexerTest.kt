package com.github.niqdev

import com.github.niqdev.bool.StringLexer
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class StringLexerTest : WordSpec({

  "StringLexer" should {

    "tokenize" {
      StringLexer.tokenize("8 < 42") shouldBe true
      StringLexer.tokenize("8 <= 42") shouldBe true
      StringLexer.tokenize("8 > 42") shouldBe false
      StringLexer.tokenize("8 >= 42") shouldBe false
    }
  }
})
