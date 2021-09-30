package com.github.niqdev.bool

// Abstract Syntax Tree
// TODO parser combinator
// https://www.toptal.com/scala/writing-an-interpreter
object Parser {

  // TODO Validated<NonEmptyList<Error>, Expression>
  fun parse(tokens: List<Token>): Expression =
    comparison(tokens)

  // comparison -> unary [ ">" | ">=" | "<" | "<=" unary ]*
  // unary
  // unary > unary
  // unary > unary > unary
  private fun comparison(tokens: List<Token>): Expression {

    fun loop(left: Expression, tmp: List<Token>): Expression =
      when {
        tmp.isEmpty() -> left
        else -> {
          val (h, t) = tmp.first() to tmp.drop(1)
          when (h) {
            is Token.TokenGreater, is Token.TokenGreaterEqual, is Token.TokenLess, is Token.TokenLessEqual ->
              Expression.Binary(left, h, comparison(t))
            else ->
              left
          }
        }
      }

    val (head, tail) = tokens.first() to tokens.drop(1)
    return loop(primary(head), tail)
  }

  /*
  // unary -> [ "!" | "-" ] primary | primary
  private fun unary(token: Token): Expression =
    when (token) {
      is Token.TokenNot, is Token.TokenMinus ->
        // TODO primary(nextToken)
        Expression.Unary(token, primary(token))
      else ->
        Parser.primary(token)
    }
   */

  // TODO missing expression
  // primary → "true" | "false" | NUMBER | STRING | KEY | "(" expression ")"
  private fun primary(token: Token): Expression =
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

fun main() {
  // 8 < 42 AND 6 > 3
  println(Expression.pretty(Parser.parse(StringLexer.tokenize("8 < 42"))))
}
