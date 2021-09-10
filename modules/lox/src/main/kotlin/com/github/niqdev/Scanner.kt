package com.github.niqdev

// or Lexer
class Scanner(private val source: String) {

  private val tokens = mutableListOf<Token>()
  private var start = 0
  private var current = 0
  private var line = 0

  fun scanTokens(): List<Token> {
    while (!isAtEnd()) {
      // we are at the beginning of the next lexeme
      start = current
      scanToken()
    }
    tokens.add(Token(TokenType.EOF, "", "TODO", line))
    return tokens
  }

  fun isAtEnd(): Boolean = current >= source.length

  fun scanToken() {
    val c = advance()
    when (c) {
      '(' -> addToken(TokenType.LEFT_PAREN)
      else ->
        Lox.reportError(line, "Unexpected character: $c")
    }
  }

  fun advance(): Char = source[current++]

  fun addToken(type: TokenType) {
    val text = source.substring(start, current)
    tokens.add(Token(type, text, "TODO", line))
  }
}
