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
        match(TokenType.FUN) -> funDeclaration("function")
        match(TokenType.VAR) -> varDeclaration()
        else -> statement()
      }
    } catch (error: ParseError) {
      println(">>> declaration: ${error.message}")
      synchronize()
      Stmt.Empty
    }

  private fun funDeclaration(kind: String): Stmt {
    val name = consume(TokenType.IDENTIFIER, "Expected [$kind] name")
    consume(TokenType.LEFT_PAREN, "Expected '(' after [$kind] name")

    // see C&P in `finishCall`
    val parameters = mutableListOf<Token>()
    if (!check(TokenType.RIGHT_PAREN)) {
      do {
        if (parameters.size >= 255) {
          error(peek(), "Can't have more than 255 parameters")
        }
        parameters.add(consume(TokenType.IDENTIFIER, "Expected parameter name"))
      } while (match(TokenType.COMMA))
    }
    consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters")

    consume(TokenType.LEFT_BRACE, "Expected '{' before [$kind] body")
    return Stmt.Function(name, parameters, blockStatements())
  }

  private fun varDeclaration(): Stmt {
    val name = consume(TokenType.IDENTIFIER, "Expected variable name")

    val initializer =
      if (match(TokenType.EQUAL)) expression()
      else Expr.Empty

    consume(TokenType.SEMICOLON, "Expected ';' after variable declaration")
    return Stmt.Var(name, initializer)
  }

  private fun synchronize(): Stmt = TODO()

  private fun statement(): Stmt =
    when {
      match(TokenType.FOR) -> forStatement()
      match(TokenType.IF) -> ifStatement()
      match(TokenType.LEFT_BRACE) -> blockStatement()
      match(TokenType.PRINT) -> printStatement()
      match(TokenType.WHILE) -> whileStatement()
      else -> expressionStatement()
    }

  private fun forStatement(): Stmt {
    consume(TokenType.LEFT_PAREN, "Expected '(' after 'for'")

    val initializer = when {
      match(TokenType.SEMICOLON) -> Stmt.Empty
      match(TokenType.VAR) -> varDeclaration()
      else -> expressionStatement()
    }

    val condition =
      if (check(TokenType.SEMICOLON)) Expr.Literal(true)
      else expression()

    consume(TokenType.SEMICOLON, "Expected ';' after loop condition")

    val increment =
      if (check(TokenType.RIGHT_PAREN)) Expr.Empty
      else expression()

    consume(TokenType.RIGHT_PAREN, "Expected ')' after for clauses")

    // return block is equivalent to
    /*
    var body = statement()

    if (increment != null) {
        body = Statement.Block(listOf(body, Statement.Expression(increment)))
    }
    body = Statement.While(condition, body)

    return if (initializer != null) Statement.Block(listOf(initializer, body))
    else body
    */

  /*
   * for (var i = 0; i < 10; i = i + 1) print i;
   *
   * is syntactic sugar for
   *
   * {
   *   var i = 0;
   *   while (i < 10) {
   *     print i;
   *     i = i + 1;
   *   }
   * }
   */
    return Stmt.Block(
      listOf(
        initializer,
        Stmt.While(
          condition,
          Stmt.Block(
            listOf(
              statement(), // body
              Stmt.Expression(increment)
            )
          )
        )
      )
    )
  }

  private fun ifStatement(): Stmt {
    consume(TokenType.LEFT_PAREN, "Expected '(' after 'if'")
    val condition = expression()
    consume(TokenType.RIGHT_PAREN, "Expected ')' after if condition'")

    val thenBranch = statement()
    // the `else` is bound to the nearest `if` that precedes it
    return when {
      match(TokenType.ELSE) -> Stmt.If(condition, thenBranch, statement())
      else -> Stmt.If(condition, thenBranch, Stmt.Empty)
    }
  }

  private fun printStatement(): Stmt {
    val expr = expression()
    consume(TokenType.SEMICOLON, "Expected ';' after value")
    return Stmt.Print(expr)
  }

  private fun blockStatements(): List<Stmt> {
    val statements = mutableListOf<Stmt>()
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration())
    }

    consume(TokenType.RIGHT_BRACE, "Expected '}' after block")
    return statements
  }
  private fun blockStatement(): Stmt = Stmt.Block(blockStatements())

  private fun whileStatement(): Stmt {
    consume(TokenType.LEFT_PAREN, "Expected '(' after 'while'")
    val condition = expression()
    consume(TokenType.RIGHT_PAREN, "Expected ')' after while condition'")

    val body = statement()
    return Stmt.While(condition, body)
  }

  private fun expressionStatement(): Stmt {
    val expr = expression()
    consume(TokenType.SEMICOLON, "Expected ';' after expression")
    return Stmt.Expression(expr)
  }

  private fun expression(): Expr = assignment()

  private fun assignment(): Expr {
    val expr = or()

    if (match(TokenType.EQUAL)) {
      val equals = previous()
      val value = assignment()

      when (expr) {
        is Expr.Variable -> return Expr.Assign(expr.name, value)
        else -> throw error(equals, "Invalid assignment target")
      }
    }
    return expr
  }

  private fun or(): Expr {
    var expr: Expr = and()

    while (match(TokenType.OR)) {
      val operator: Token = previous()
      val right: Expr = and()
      expr = Expr.Logical(expr, operator, right)
    }

    return expr
  }

  private fun and(): Expr {
    var expr: Expr = equality()

    while (match(TokenType.AND)) {
      val operator: Token = previous()
      val right: Expr = equality()
      expr = Expr.Logical(expr, operator, right)
    }

    return expr
  }

  private fun equality(): Expr {
    var expr: Expr = comparison()

    // if the parser never encounters an equality operator, then it never enters the loop
    // in that way, this method matches an equality operator or anything of higher precedence
    // lookahead: `match` consumes next token (increment/advance index) and peek to previous one
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
    return call()
  }

  private fun call(): Expr {
    var expr = primary()

    while (true) {
      if (match(TokenType.LEFT_PAREN)) {
        expr = finishCall(expr)
      } else {
        break
      }
    }

    return expr
  }

  // builds arguments list: zero or more
  private fun finishCall(callee: Expr): Expr {
    val arguments = mutableListOf<Expr>()
    if (!check(TokenType.RIGHT_PAREN)) {
      do {
        // maximum argument counts:
        // - the C standard says a conforming implementation has to support at least 127 arguments to a function, but no upper limit
        // - the Java specification says a method can accept no more than 255 arguments
        // The limit is 254 arguments if the method is an instance method,  because `this` - the receiver of the method - works like an argument that is implicitly passed to the method
        if (arguments.size >= 255) {
          error(peek(), "Can't have more than 255 arguments")
        }
        arguments.add(expression())
      } while (match(TokenType.COMMA))
    }
    val paren = consume(TokenType.RIGHT_PAREN, "Expected ')' after arguments")
    return Expr.Call(callee, paren, arguments)
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
  private fun check(type: TokenType): Boolean =
    if (isAtEnd()) false
    else peek().type == type

  // consumes the current token and returns it
  private fun advance(): Token {
    if (!isAtEnd()) current++
    return previous()
  }

  private fun isAtEnd(): Boolean = peek().type == TokenType.EOF

  // returns the current token we have yet to consume
  private fun peek(): Token = tokens[current]

  // returns the most recently consumed token
  private fun previous(): Token = tokens[current - 1]

  private fun consume(type: TokenType, message: String): Token =
    if (check(type)) advance()
    else throw error(peek(), message)

  private class ParseError : RuntimeException()

  private fun error(token: Token, message: String): ParseError {
    Lox.error(token, message)
    return ParseError()
  }
}
