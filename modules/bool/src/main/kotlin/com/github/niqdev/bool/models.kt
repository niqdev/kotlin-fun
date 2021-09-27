package com.github.niqdev.bool

// TODO delete
enum class TokenType {
  // single-character tokens
  LEFT_PAREN,
  RIGHT_PAREN,

  // one or two character tokens
  BANG,
  BANG_EQUAL,
  EQUAL_EQUAL,
  GREATER,
  GREATER_EQUAL,
  LESS,
  LESS_EQUAL,

  // literals
  STRING,
  NUMBER,

  // keywords
  AND,
  OR
}

sealed class Token {
  internal data class TokenInt(val value: Int) : Token()
  internal data class TokenString(val value: String) : Token()
  internal object TokenLess : Token()

  companion object {
    fun pretty(token: Token) =
      when (token) {
        is TokenInt -> "Int(${token.value})"
        is TokenString -> "String(${token.value})"
        is TokenLess -> "Symbol(<)"
      }
  }
}
