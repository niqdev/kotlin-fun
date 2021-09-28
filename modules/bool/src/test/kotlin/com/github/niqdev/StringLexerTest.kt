package com.github.niqdev

import com.github.niqdev.bool.StringLexer
import com.github.niqdev.bool.Token
import com.github.niqdev.bool.internal.toList
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class StringLexerTest : WordSpec({

  "StringLexer" should {

    "tokenize" {
      val input = "( ) != = == > >= < <= AND and && OR oR || NOT NOt ! 42 simple_key key.sub-nested \"a str@ing\" 8"
      val result = listOf(
        Token.TokenLeftParentheses,
        Token.TokenRightParentheses,
        Token.TokenBangEqual,
        Token.TokenEqual,
        Token.TokenEqualEqual,
        Token.TokenGreater,
        Token.TokenGreaterEqual,
        Token.TokenLess,
        Token.TokenLessEqual,
        Token.TokenAnd,
        Token.TokenAnd,
        Token.TokenAnd,
        Token.TokenOr,
        Token.TokenOr,
        Token.TokenOr,
        Token.TokenNot,
        Token.TokenNot,
        Token.TokenNot,
        Token.TokenInt(42),
        Token.TokenKey("simple_key"),
        Token.TokenKey("key.sub-nested"),
        Token.TokenString("a str@ing"),
        Token.TokenInt(8)
      )
      StringLexer.tokenize(input).toList() shouldBe result
    }
  }
})
