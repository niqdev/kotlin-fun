package com.github.niqdev.bool

// Abstract Syntax Tree
object Parser {

  // TODO Validated<NonEmptyList<Error>, Expression>
  fun parse(tokens: List<Token>): Expression {

    // while iterating process head and pass around tail - index ?
    tailrec fun loop(tmp: List<Token>, expression: Expression?): Expression =
      when {
        tmp.isEmpty() -> {
          // TODO Option<Expression>
          println(Expression.pretty(expression!!))
          null as Expression
        }
        else -> {
          val (head, tail) = tmp.first() to tmp.drop(1)
          expression as Expression
        }
      }

    return loop(tokens, null)
  }

  // unary → ( "!" | "-" ) primary
  private fun unary(token: Token): Expression = TODO()
//    when (token) {
//      is Token.TokenNot, is Token.TokenMinus ->
//        Expression.Unary(token, primary("TODO"))
//      else -> primary("TODO")
//    }

  // TODO missing grouping
  private fun primary(token: Token): Expression =
    when (token) {
      is Token.TokenTrue -> Expression.Literal(Value.ValueTrue)
      is Token.TokenFalse -> Expression.Literal(Value.ValueFalse)
      is Token.TokenNumber -> Expression.Literal(Value.ValueNumber(token.value))
      is Token.TokenString -> Expression.Literal(Value.ValueString(token.value))
      is Token.TokenKey -> Expression.Literal(Value.ValueKey(token.value))
      else -> error("invalid token: $token") // TODO Validated
    }
}
