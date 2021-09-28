package com.github.niqdev

import com.github.niqdev.bool.StringLexer
import com.github.niqdev.bool.Token
import com.github.niqdev.bool.internal.InternalList
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class StringLexerTest : WordSpec({

  "StringLexer" should {

    "tokenize" {
      val expected = InternalList(Token.TokenInt(8), Token.TokenLess, Token.TokenInt(42))
      StringLexer.tokenize("8 < 42") shouldBe expected
    }
  }
})
