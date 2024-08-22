package com.github.niqdev.bool

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class StringLexerTest :
  WordSpec({

    "StringLexer" should {

      "tokenize" {
        val input = "( ) != = == > >= < <= tRue False AND and && OR oR || NOT NOt ! IN MATCH 42 simple_key key.sub-nested \"a str@ing\" 8"
        val result =
          listOf(
            Token.LeftParentheses,
            Token.RightParentheses,
            Token.BangEqual,
            Token.Equal,
            Token.EqualEqual,
            Token.Greater,
            Token.GreaterEqual,
            Token.Less,
            Token.LessEqual,
            Token.True,
            Token.False,
            Token.And,
            Token.And,
            Token.And,
            Token.Or,
            Token.Or,
            Token.Or,
            Token.Not,
            Token.Not,
            Token.Not,
            Token.In,
            Token.Match,
            Token.Number(42),
            Token.TokenKey("simple_key"),
            Token.TokenKey("key.sub-nested"),
            Token.String("a str@ing"),
            Token.Number(8),
          )
        StringLexer.tokenize(input) shouldBe result
      }
    }
  })
