package com.github.niqdev.bool

// TODO missing implementations:
// - key: e.g. json path
// - "IN a, b, b"
// - "MATCH /aaa/"
// - grouping e.g. "(" expression ")"
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

  // and -> equality [ "AND" equality ]*
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

  // comparison -> unary [ ">" | ">=" | "<" | "<=" unary ]*
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

    val (currentTokens, result) = unary(tokens)
    return loop(currentTokens, result)
  }

  // unary -> [ "!" ] primary | primary
  private fun unary(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {
    val (head, tail) = tokens.first() to tokens.drop(1)

    return when (head) {
      is Token.Not -> {
        val (nextTokens, right) = unary(tail)
        nextTokens to FreeB.Not(right)
      }
      else -> tail to primary(head)
    }
  }

  // TODO grouping/expressions
  // primary -> "true" | "false" | NUMBER | STRING | KEY
  private fun primary(token: Token): FreeB<Predicate> =
    when (token) {
      is Token.True -> FreeB.True()
      is Token.False -> FreeB.False()
      is Token.Number -> FreeB.Pure(Predicate.Identity(Value.Number(token.int)))
      is Token.String -> FreeB.Pure(Predicate.Identity(Value.String(token.string)))
      is Token.TokenKey -> FreeB.Pure(Predicate.Identity(Value.Key(token.key)))
      else -> error("invalid token: $token") // TODO Validated
    }
}

fun main() {
  println(Parser.parse(StringLexer.tokenize("8 < 42 == 6 > 3 AND 4 < 9")).pretty())
}
