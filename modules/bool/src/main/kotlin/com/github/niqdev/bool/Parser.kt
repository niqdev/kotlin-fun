package com.github.niqdev.bool

// Abstract Syntax Tree
// TODO parser combinator
// https://www.toptal.com/scala/writing-an-interpreter
object Parser {

  // TODO Validated<NonEmptyList<Error>, Expression>
  fun parse(tokens: List<Token>): FreeB<Predicate> = expression(tokens)

  private fun expression(tokens: List<Token>): FreeB<Predicate> = and(tokens).second

  private fun and(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {

    fun loop(currentTokens: List<Token>, left: FreeB<Predicate>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        currentTokens.isEmpty() -> emptyList<Token>() to left
        else -> {
          val (head, tail) = currentTokens.first() to currentTokens.drop(1)
          when (head) {
            is Token.TokenAnd -> {
              val (nextTokens, right) = and(tail)
              nextTokens to FreeB.And(left, right)
            }
            else -> currentTokens to left
          }
        }
      }

    val (currentTokens, result) = equality(tokens)
    return loop(currentTokens, result)
  }

  private fun equality(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {

    fun loop(currentTokens: List<Token>, left: FreeB<Predicate>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        currentTokens.isEmpty() -> emptyList<Token>() to left
        else -> {
          val (head, tail) = currentTokens.first() to currentTokens.drop(1)
          when (head) {
            is Token.TokenBangEqual -> {
              val (nextTokens, right) = equality(tail)
              nextTokens to FreeB.Pure(Predicate.BangEqual(left, right))
            }
            is Token.TokenEqualEqual -> {
              val (nextTokens, right) = equality(tail)
              nextTokens to FreeB.Pure(Predicate.EqualEqual(left, right))
            }
            else -> currentTokens to left
          }
        }
      }

    val (currentTokens, result) = comparison(tokens)
    return loop(currentTokens, result)
  }

  private fun comparison(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {

    fun loop(currentTokens: List<Token>, left: FreeB<Predicate>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        currentTokens.isEmpty() -> emptyList<Token>() to left
        else -> {
          val (head, tail) = currentTokens.first() to currentTokens.drop(1)
          when (head) {
            is Token.TokenGreater -> {
              val (nextTokens, right) = comparison(tail)
              nextTokens to FreeB.Pure(Predicate.Greater(left, right))
            }
            is Token.TokenGreaterEqual -> {
              val (nextTokens, right) = comparison(tail)
              nextTokens to FreeB.Pure(Predicate.GreaterEqual(left, right))
            }
            is Token.TokenLess -> {
              val (nextTokens, right) = comparison(tail)
              nextTokens to FreeB.Pure(Predicate.Less(left, right))
            }
            is Token.TokenLessEqual -> {
              val (nextTokens, right) = comparison(tail)
              nextTokens to FreeB.Pure(Predicate.LessEqual(left, right))
            }
            else -> currentTokens to left
          }
        }
      }

    val (head, tail) = tokens.first() to tokens.drop(1)
    return loop(tail, primary(head))
  }

  private fun primary(token: Token): FreeB<Predicate> =
    when (token) {
      is Token.TokenTrue -> FreeB.True()
      is Token.TokenFalse -> FreeB.False()
      is Token.TokenNumber -> FreeB.Pure(Predicate.NumberValue(token.value))
      is Token.TokenString -> FreeB.Pure(Predicate.StringValue(token.value))
      // is Token.TokenKey -> Expression.Literal(Value.KeyValue(token.value))
      // is Token.TokenLeftParentheses -> Expression.Literal(Value.KeyValue(token.value))
      else -> error("invalid token: $token") // TODO Validated
    }
}

/*
private data class ExpressionResult(
    val expression: Expression,
    val tokens: List<Token> = listOf()
  )

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
 */
