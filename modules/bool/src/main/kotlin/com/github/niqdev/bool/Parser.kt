package com.github.niqdev.bool

object Parser {

  // TODO Validated<NonEmptyList<Error>, Expression>
  // recursive descent parser: given a valid sequence of tokens, produce a corresponding Abstract Syntax Tree
  fun parse(tokens: List<Token>): FreeB<Predicate> =
    expression(tokens)

  // expression -> or
  private fun expression(tokens: List<Token>): FreeB<Predicate> =
    or(tokens).second

  // or -> and [ "OR and ]*
  private fun or(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {

    fun loop(currentTokens: List<Token>, left: FreeB<Predicate>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        currentTokens.isEmpty() -> emptyList<Token>() to left
        else -> {
          val (head, tail) = currentTokens.first() to currentTokens.drop(1)
          when (head) {
            is Token.TokenOr -> {
              val (nextTokens, right) = or(tail)
              nextTokens to FreeB.Or(left, right)
            }
            else -> currentTokens to left
          }
        }
      }

    val (currentTokens, result) = and(tokens)
    return loop(currentTokens, result)
  }

  // and -> equality [ "AND" equality ]*
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

  // equality -> comparison [ "!=" | "==" comparison ]*
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

  // comparison -> unary [ ">" | ">=" | "<" | "<=" unary ]*
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
    // TODO debug unary
    // val (currentTokens, result) = unary(tokens)
    // return loop(currentTokens, result)
  }

  // unary -> [ "!" | "-" ] primary | primary
  private fun unary(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {

    fun loop(currentTokens: List<Token>, left: FreeB<Predicate>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        currentTokens.isEmpty() -> emptyList<Token>() to left
        else -> {
          val (head, tail) = currentTokens.first() to currentTokens.drop(1)
          when (head) {
            is Token.TokenNot -> {
              val (nextTokens, right) = unary(tail)
              nextTokens to FreeB.Not(right)
            }
            is Token.TokenMinus -> {
              val (nextTokens, right) = unary(tail)
              nextTokens to FreeB.Pure(Predicate.Minus(right))
            }
            else -> currentTokens to left
          }
        }
      }

    val (head, tail) = tokens.first() to tokens.drop(1)
    return loop(tail, primary(head))
  }

  // TODO "(" expression ")"
  // primary -> "true" | "false" | NUMBER | STRING | KEY
  private fun primary(token: Token): FreeB<Predicate> =
    when (token) {
      is Token.TokenTrue -> FreeB.True()
      is Token.TokenFalse -> FreeB.False()
      is Token.TokenNumber -> FreeB.Pure(Predicate.Identity(Value.NumberValue(token.value)))
      is Token.TokenString -> FreeB.Pure(Predicate.Identity(Value.StringValue(token.value)))
      is Token.TokenKey -> FreeB.Pure(Predicate.Identity(Value.KeyValue(token.value)))
      // is Token.TokenLeftParentheses -> TODO()
      else -> error("invalid token: $token") // TODO Validated
    }
}

fun main() {
  println(Parser.parse(StringLexer.tokenize("8 < 42 == 6 > 3 AND 4 < 9")).pretty())
}
