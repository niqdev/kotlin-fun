package com.github.niqdev

// >>> (2)

data class Token(
  val type: TokenType,
  // raw substrings of the source code
  val lexeme: String,
  // TODO Value
  val literal: Any?,
  val line: Int,
)

fun Token.pretty(): String = "type: $type | lexeme: $lexeme | literal: $literal | line: $line"

enum class TokenType {
  // single-character tokens
  LEFT_PAREN,
  RIGHT_PAREN,
  LEFT_BRACE,
  RIGHT_BRACE,
  COMMA,
  DOT,
  MINUS,
  PLUS,
  SEMICOLON,
  SLASH,
  STAR,

  // one or two character tokens
  BANG,
  BANG_EQUAL,
  EQUAL,
  EQUAL_EQUAL,
  GREATER,
  GREATER_EQUAL,
  LESS,
  LESS_EQUAL,

  // literals
  IDENTIFIER,
  STRING,
  NUMBER,

  // keywords
  AND,
  CLASS,
  ELSE,
  FALSE,
  FUN,
  FOR,
  IF,
  NIL,
  OR,
  PRINT,
  RETURN,
  SUPER,
  THIS,
  TRUE,
  VAR,
  WHILE,

  EOF,
}
