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

  private fun scanToken(): Unit =
    when (val c = advance()) {
      '(' -> addToken(TokenType.LEFT_PAREN)
      ')' -> addToken(TokenType.RIGHT_PAREN)
      '{' -> addToken(TokenType.LEFT_BRACE)
      '}' -> addToken(TokenType.RIGHT_BRACE)
      ',' -> addToken(TokenType.COMMA)
      '.' -> addToken(TokenType.DOT)
      '-' -> addToken(TokenType.MINUS)
      '+' -> addToken(TokenType.PLUS)
      ';' -> addToken(TokenType.SEMICOLON)
      '*' -> addToken(TokenType.STAR)

      '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
      '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
      '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
      '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)

      '/' ->
        if (match('/')) {
          // a comment goes until the end of the line
          while (peek() != '\n' && !isAtEnd()) advance()
        } else addToken(TokenType.SLASH)

      ' ', '\r', '\t' -> {}
      '\n' -> incrementLine()
      else ->
        Lox.reportError(line, "Unexpected character: $c")
    }

  private fun isAtEnd(): Boolean = current >= source.length

  private fun advance(): Char = source[incrementOffset()]

  private fun match(expected: Char): Boolean {
    if (isAtEnd()) return false
    if (source[current] != expected) return false
    incrementOffset()
    return true
  }

  // lookahead
  private fun peek(): Char = if (isAtEnd()) '\u0000' else source[current]

  private fun incrementOffset(): Int = current++
  private fun incrementLine() { line++ ; return }

  private fun addToken(type: TokenType) {
    val text = source.substring(start, current)
    tokens.add(Token(type, text, "TODO", line))
  }
}
