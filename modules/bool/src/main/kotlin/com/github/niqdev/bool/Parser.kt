package com.github.niqdev.bool

// Abstract Syntax Tree
object Parser {

  // TODO Validated<NonEmptyList<Error>, Expression>
  fun parse(tokens: List<Token>): Expression =
    comparison(tokens)

  // comparison -> unary [ ">" | ">=" | "<" | "<=" unary ]*
  // unary
  // unary > unary
  // unary > unary > unary
  private fun comparison(tokens: List<Token>): Expression =
    when {
      tokens.isEmpty() -> TODO()
      else -> {
        val (head, tail) = tokens.first() to tokens.drop(1)

        when (tail.first()) {
          is Token.TokenGreater, is Token.TokenGreaterEqual, is Token.TokenLess, is Token.TokenLessEqual ->
            Expression.Binary(unary(head), head, comparison(tail))
          else ->
            comparison(tail)
        }

        unary(head)
      }
    }

  // unary -> [ "!" | "-" ] primary | primary
  private fun unary(token: Token): Expression =
    when (token) {
      is Token.TokenNot, is Token.TokenMinus ->
        // TODO primary(nextToken)
        Expression.Unary(token, primary(token))
      else ->
        primary(token)
    }

  // TODO missing expression
  // primary → "true" | "false" | NUMBER | STRING | KEY | "(" expression ")"
  private fun primary(token: Token): Expression.Literal =
    when (token) {
      is Token.TokenTrue -> Expression.Literal(Value.TrueValue)
      is Token.TokenFalse -> Expression.Literal(Value.FalseValue)
      is Token.TokenNumber -> Expression.Literal(Value.NumberValue(token.value))
      is Token.TokenString -> Expression.Literal(Value.StringString(token.value))
      is Token.TokenKey -> Expression.Literal(Value.KeyValue(token.value))
      // is Token.TokenLeftParentheses -> Expression.Literal(Value.KeyValue(token.value))
      else -> error("invalid token: $token") // TODO Validated
    }
}
