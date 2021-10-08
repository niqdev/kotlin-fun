package com.github.niqdev

// >>> (5)

/**
 * recursive descent parser: top-down parser because it starts from the top or outermost grammar rule i.e. `expression`
 * and works its way down into the nested subexpressions before finally reaching the leaves of the syntax tree
 *
 * Given a valid sequence of tokens, produce a corresponding syntax tree
 * Given an invalid sequence of tokens, detect any errors and tell the user about their mistakes
 */
class Parser(private val tokens: List<Token>) {
  private var current = 0

  fun parse(): List<Stmt> {
    val statements = mutableListOf<Stmt>()
    while (!isAtEnd()) {
      statements.add(declaration())
    }
    return statements
  }

  private fun declaration(): Stmt =
    try {
      when {
        match(TokenType.VAR) -> varDeclaration()
        else -> statement()
      }
    } catch (error: ParseError) {
      synchronize()
      Stmt.Empty
    }

  private fun varDeclaration(): Stmt {
    val name = consume(TokenType.IDENTIFIER, "Expected variable name")
    var initializer: Expr = Expr.Empty
    if (match(TokenType.EQUAL)) {
      initializer = expression()
    }
    consume(TokenType.SEMICOLON, "Expected ';' after variable declaration")
    return Stmt.Var(name, initializer)
  }
  private fun synchronize(): Stmt = TODO()

  private fun statement(): Stmt =
    when {
      match(TokenType.PRINT) -> printStatement()
      else -> expressionStatement()
    }

  private fun printStatement(): Stmt {
    val expr = expression()
    consume(TokenType.SEMICOLON, "Expected ';' after value")
    return Stmt.Print(expr)
  }

  private fun expressionStatement(): Stmt {
    val expr = expression()
    consume(TokenType.SEMICOLON, "Expected ';' after expression")
    return Stmt.Expression(expr)
  }

  private fun expression(): Expr = equality()

  private fun equality(): Expr {
    var expr: Expr = comparison()

    // if the parser never encounters an equality operator, then it never enters the loop
    // in that way, this method matches an equality operator or anything of higher precedence
    while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      val operator: Token = previous()
      val right: Expr = comparison()
      expr = Expr.Binary(expr, operator, right)
    }

    return expr
  }

  private fun comparison(): Expr {
    var expr: Expr = term()

    while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
      val operator: Token = previous()
      val right: Expr = term()
      expr = Expr.Binary(expr, operator, right)
    }

    return expr
  }

  private fun term(): Expr {
    var expr: Expr = factor()

    while (match(TokenType.MINUS, TokenType.PLUS)) {
      val operator: Token = previous()
      val right: Expr = factor()
      expr = Expr.Binary(expr, operator, right)
    }

    return expr
  }

  private fun factor(): Expr {
    var expr: Expr = unary()

    while (match(TokenType.SLASH, TokenType.STAR)) {
      val operator: Token = previous()
      val right: Expr = unary()
      expr = Expr.Binary(expr, operator, right)
    }

    return expr
  }

  private fun unary(): Expr {
    if (match(TokenType.BANG, TokenType.MINUS)) {
      val operator: Token = previous()
      val right: Expr = unary()
      return Expr.Unary(operator, right)
    }
    return primary()
  }

  private fun primary(): Expr =
    when {
      match(TokenType.FALSE) -> Expr.Literal(false)
      match(TokenType.TRUE) -> Expr.Literal(true)
      match(TokenType.NIL) -> Expr.Literal(null)
      match(TokenType.NUMBER, TokenType.STRING) -> Expr.Literal(previous().literal)
      match(TokenType.LEFT_PAREN) -> {
        val expr = expression()
        consume(TokenType.RIGHT_PAREN, "Expected ')' after expression")
        Expr.Grouping(expr)
      }
      match(TokenType.IDENTIFIER) -> Expr.Variable(previous())
      else -> throw error(peek(), "Expected expression")
    }

  // checks to see if the current token has any of the given types
  private fun match(vararg types: TokenType): Boolean {
    for (type in types) {
      if (check(type)) {
        advance()
        return true
      }
    }
    return false
  }

  // returns if the current token is of the given type
  private fun check(type: TokenType): Boolean {
    if (isAtEnd()) return false
    return peek().type == type
  }

  // consumes the current token and returns it
  private fun advance(): Token {
    if (!isAtEnd()) current++
    return previous()
  }

  private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

  // returns the current token we have yet to consume
  private fun peek(): Token = tokens.get(current)

  // returns the most recently consumed token
  private fun previous(): Token = tokens.get(current - 1)

  private fun consume(type: TokenType, message: String): Token {
    if (check(type)) return advance()
    throw error(peek(), message)
  }

  private class ParseError : RuntimeException()

  private fun error(token: Token, message: String): ParseError {
    Lox.error(token, message)
    return ParseError()
  }
}