package com.github.niqdev.bool

// Abstract Syntax Tree
// TODO parser combinator
// https://www.toptal.com/scala/writing-an-interpreter
object Parser {

  private data class ExpressionResult(
    val expression: Expression,
    val tokens: List<Token> = listOf()
  )

  // TODO Validated<NonEmptyList<Error>, Expression>
  fun parse(tokens: List<Token>): Expression {
    val (expression, _) = and(tokens)
    return expression
  }

  // TODO
  // private fun and0() = process(::and0, Token.TokenAnd): (List<Token>) -> ExpressionResult

  private fun process(nextParser: ((List<Token>) -> ExpressionResult), vararg matchingTokens: Token): (List<Token>) -> ExpressionResult =
    { tokens ->

      fun loop(left: Expression, tmp: List<Token>): ExpressionResult =
        when {
          tmp.isEmpty() -> ExpressionResult(left)
          matchingTokens.contains(tmp.first()) -> {
            val (h, t) = tmp.first() to tmp.drop(1)
            val (right, newTmp) = nextParser(t)
            ExpressionResult(Expression.Binary(left, h, right), newTmp)
          }
          else -> ExpressionResult(left, tmp)
        }

      val result = nextParser(tokens)
      loop(result.expression, result.tokens)
    }

  private fun and(tokens: List<Token>): ExpressionResult {

    fun loop(left: Expression, tmp: List<Token>): ExpressionResult =
      when {
        tmp.isEmpty() -> ExpressionResult(left)
        else -> {
          val (h, t) = tmp.first() to tmp.drop(1)
          when (h) {
            is Token.TokenAnd -> {
              val (right, newTmp) = and(t)
              ExpressionResult(Expression.Binary(left, h, right), newTmp)
            }

            else -> ExpressionResult(left, tmp)
          }
        }
      }

    val (tmp, result) = equality(tokens)
    return loop(result, tmp)
  }

  private fun equality(tokens: List<Token>): Pair<List<Token>, Expression> {

    fun loop(left: Expression, tmp: List<Token>): Pair<List<Token>, Expression> =
      when {
        tmp.isEmpty() -> tmp to left
        else -> {
          val (h, t) = tmp.first() to tmp.drop(1)
          when (h) {
            is Token.TokenBangEqual, is Token.TokenEqualEqual -> {
              val (newTmp, result) = equality(t)
              newTmp to Expression.Binary(left, h, result)
            }

            else ->
              tmp to left
          }
        }
      }
    val (tmp, result) = comparison(tokens)
    return loop(result, tmp)
  }

  // comparison -> unary [ ">" | ">=" | "<" | "<=" unary ]*
  // unary
  // unary > unary
  // unary > unary > unary
  private fun comparison(tokens: List<Token>): Pair<List<Token>, Expression> {

    fun loop(left: Expression, tmp: List<Token>): Pair<List<Token>, Expression> =
      when {
        tmp.isEmpty() -> tmp to left
        else -> {
          val (h, t) = tmp.first() to tmp.drop(1)
          when (h) {
            is Token.TokenGreater, is Token.TokenGreaterEqual, is Token.TokenLess, is Token.TokenLessEqual -> {
              val (newTmp, result) = comparison(t)
              newTmp to Expression.Binary(left, h, result)
            }
            else ->
              tmp to left
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
  println(Expression.pretty(Parser.parse(StringLexer.tokenize("8 AND 42 == 6 > 3"))))
}
