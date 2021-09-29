package com.github.niqdev

import com.github.niqdev.bool.StringLexer
import com.github.niqdev.bool.Token
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class StringLexerTest : WordSpec({

  "StringLexer" should {

    "tokenize" {
      val input = "( ) - != = == > >= < <= tRue False AND and && OR oR || NOT NOt ! 42 simple_key key.sub-nested \"a str@ing\" 8"
      val result = listOf(
        Token.TokenLeftParentheses,
        Token.TokenRightParentheses,
        Token.TokenMinus,
        Token.TokenBangEqual,
        Token.TokenEqual,
        Token.TokenEqualEqual,
        Token.TokenGreater,
        Token.TokenGreaterEqual,
        Token.TokenLess,
        Token.TokenLessEqual,
        Token.TokenTrue,
        Token.TokenFalse,
        Token.TokenAnd,
        Token.TokenAnd,
        Token.TokenAnd,
        Token.TokenOr,
        Token.TokenOr,
        Token.TokenOr,
        Token.TokenNot,
        Token.TokenNot,
        Token.TokenNot,
        Token.TokenNumber(42),
        Token.TokenKey("simple_key"),
        Token.TokenKey("key.sub-nested"),
        Token.TokenString("a str@ing"),
        Token.TokenNumber(8)
      )
      StringLexer.tokenize(input) shouldBe result
    }
  }
})
