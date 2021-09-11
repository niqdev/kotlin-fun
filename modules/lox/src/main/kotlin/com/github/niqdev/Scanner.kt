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

      '/' -> scanSlash()

      ' ', '\r', '\t' -> {
      }
      '\n' -> incrementLine()

      '"' -> scanString()
      else ->
        when {
          c.isDigit() -> scanNumber()
          c.isAlpha() -> scanIdentifier()
          else -> Lox.reportError(line, "Unexpected character: $c")
        }
    }

  private fun addToken(type: TokenType, literal: Any? = null) {
    val text = source.substring(start, current)
    tokens.add(Token(type, text, literal, line))
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
  private fun peekNext(): Char = if (current + 1 >= source.length) '\u0000' else source[current + 1]

  private fun incrementOffset(): Int = current++
  private fun incrementLine() {
    line++; return
  }

  private fun scanSlash() {
    if (match('/')) {
      // a comment goes until the end of the line
      while (peek() != '\n' && !isAtEnd()) advance()
    } else addToken(TokenType.SLASH)
  }

  private fun scanString() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++
      advance()
    }

    if (isAtEnd()) {
      Lox.reportError(line, "Unterminated string")
      return
    }

    // the closing "
    advance()
    // trim the surrounding quotes
    val value = source.substring(start + 1, current - 1)
    addToken(TokenType.STRING, value)
  }

  private fun scanNumber() {
    while (peek().isDigit()) advance()

    // look for a fractional part
    if (peek() == '.' && peekNext().isDigit()) {
      // consume the "."
      advance()

      while (peek().isDigit()) advance()
    }

    val value = source.substring(start, current).toDouble()
    addToken(TokenType.NUMBER, value)
  }

  private fun scanIdentifier() {
    while (peek().isAlphaNumeric()) advance()

    val value = source.substring(start, current)
    addToken(keywords[value] ?: TokenType.IDENTIFIER)
  }

  companion object {

    // TODO enum value
    private val keywords = mapOf(
      "and" to TokenType.AND,
      "class" to TokenType.CLASS,
      "else" to TokenType.ELSE,
      "false" to TokenType.FALSE,
      "for" to TokenType.FOR,
      "fun" to TokenType.FUN,
      "if" to TokenType.IF,
      "nil" to TokenType.NIL,
      "or" to TokenType.OR,
      "print" to TokenType.PRINT,
      "return" to TokenType.RETURN,
      "super" to TokenType.SUPER,
      "this" to TokenType.THIS,
      "true" to TokenType.TRUE,
      "var" to TokenType.VAR,
      "while" to TokenType.WHILE
    )
  }
}

private fun Char.isDigit(): Boolean = this in '0'..'9'
private fun Char.isAlpha(): Boolean = this in 'a'..'z' || this in 'A'..'Z' || this == '_'
private fun Char.isAlphaNumeric(): Boolean = this.isDigit() || this.isAlpha()
