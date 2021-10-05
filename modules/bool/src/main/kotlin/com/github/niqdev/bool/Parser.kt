package com.github.niqdev.bool

// TODO missing implementations:
// - key: e.g. json path
// - "IN a, b, b"
// - "MATCH /aaa/"
object Parser {

  // TODO Validated<NonEmptyList<Error>, FreeB<Predicate>>
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
            is Token.Or -> {
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

  // and -> not [ "AND" not ]*
  private fun and(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {

    fun loop(currentTokens: List<Token>, left: FreeB<Predicate>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        currentTokens.isEmpty() -> emptyList<Token>() to left
        else -> {
          val (head, tail) = currentTokens.first() to currentTokens.drop(1)
          when (head) {
            is Token.And -> {
              val (nextTokens, right) = and(tail)
              nextTokens to FreeB.And(left, right)
            }
            else -> currentTokens to left
          }
        }
      }

    val (currentTokens, result) = not(tokens)
    return loop(currentTokens, result)
  }

  // not -> "!" equality | equality
  private fun not(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {
    val (head, tail) = tokens.first() to tokens.drop(1)

    return when (head) {
      is Token.Not -> {
        val (nextTokens, right) = not(tail)
        nextTokens to FreeB.Not(right)
      }
      else -> {
        // continue with the same tokens
        val (currentTokens, result) = equality(tokens)
        currentTokens to result
      }
    }
  }

  // equality -> comparison [ "!=" | "==" comparison ]*
  private fun equality(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {

    fun loop(currentTokens: List<Token>, left: FreeB<Predicate>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        currentTokens.isEmpty() -> emptyList<Token>() to left
        else -> {
          val (head, tail) = currentTokens.first() to currentTokens.drop(1)
          when (head) {
            is Token.BangEqual -> {
              val (nextTokens, right) = equality(tail)
              nextTokens to FreeB.Pure(Predicate.BangEqual(left, right))
            }
            is Token.EqualEqual -> {
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

  // comparison -> primary [ ">" | ">=" | "<" | "<=" primary ]*
  private fun comparison(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {

    fun loop(currentTokens: List<Token>, left: FreeB<Predicate>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        currentTokens.isEmpty() -> emptyList<Token>() to left
        else -> {
          val (head, tail) = currentTokens.first() to currentTokens.drop(1)
          when (head) {
            is Token.Greater -> {
              val (nextTokens, right) = comparison(tail)
              nextTokens to FreeB.Pure(Predicate.Greater(left, right))
            }
            is Token.GreaterEqual -> {
              val (nextTokens, right) = comparison(tail)
              nextTokens to FreeB.Pure(Predicate.GreaterEqual(left, right))
            }
            is Token.Less -> {
              val (nextTokens, right) = comparison(tail)
              nextTokens to FreeB.Pure(Predicate.Less(left, right))
            }
            is Token.LessEqual -> {
              val (nextTokens, right) = comparison(tail)
              nextTokens to FreeB.Pure(Predicate.LessEqual(left, right))
            }
            else -> currentTokens to left
          }
        }
      }

    val (currentTokens, result) = primary(tokens)
    return loop(currentTokens, result)
  }

  // primary -> "true" | "false" | NUMBER | STRING | KEY | "(" expression ")"
  private fun primary(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {
    val (head, tail) = tokens.first() to tokens.drop(1)

    return when (head) {
      is Token.True -> tail to FreeB.True()
      is Token.False -> tail to FreeB.False()
      is Token.Number -> tail to FreeB.Pure(Predicate.Identity(Value.Number(head.int)))
      is Token.String -> tail to FreeB.Pure(Predicate.Identity(Value.String(head.string)))
      is Token.TokenKey -> tail to FreeB.Pure(Predicate.Identity(Value.Key(head.key)))
      is Token.LeftParentheses -> grouping(tail)
      else -> error("invalid token: $head") // TODO Validated
    }
  }

  // tries to match an expression between parentheses ONLY 1 level nested
  private fun grouping(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {
    fun loop(tmp: List<Token>, expressionTokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        tmp.isEmpty() ->
          error("Expected ')' after expression") // TODO Validated
        tmp.first() is Token.RightParentheses ->
          tmp.drop(1) to expression(expressionTokens)
        else ->
          loop(tmp.drop(1), expressionTokens + tmp.first())
      }
    return loop(tokens, emptyList())
  }
}

fun main() {
  println(Parser.parse(StringLexer.tokenize("8 < 42 == 6 > 3 AND 4 < 9")).pretty())
}
