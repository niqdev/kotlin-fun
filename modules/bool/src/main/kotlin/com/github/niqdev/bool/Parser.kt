package com.github.niqdev.bool

// TODO change loop signature i.e. `loop(Pair)` or replace with fold
// TODO missing implementations:
// - "IN a, b, b"
// - "MATCH /aaa/"
object Parser {

  // TODO Validated<NonEmptyList<Error>, FreeB<Predicate>>
  // recursive descent parser: given a valid sequence of tokens, produce a corresponding Abstract Syntax Tree
  fun parse(tokens: List<Token>, keys: Map<String, Token> = emptyMap()): FreeB<Predicate> =
    expression(resolveKeys(tokens, keys))

  private fun resolveKeys(tokens: List<Token>, keys: Map<String, Token>): List<Token> =
    tokens.map {
      when (it) {
        is Token.TokenKey -> keys.getOrDefault(it.key, it) // TODO Validated
        else -> it
      }
    }

  // expression -> or
  private fun expression(tokens: List<Token>): FreeB<Predicate> =
    or(tokens).second

  // or -> and [ "OR" and ]*
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

  // not -> [ "NOT" ] equality
  private fun not(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> =
    when {
      tokens.isEmpty() -> equality(tokens)
      else -> {
        val (head, tail) = tokens.first() to tokens.drop(1)
        when (head) {
          is Token.Not -> {
            val (nextTokens, right) = not(tail)
            nextTokens to FreeB.Not(right)
          }
          else -> equality(tokens)
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
      is Token.LeftParentheses -> grouping(tail)
      else -> error("invalid token: $head") // TODO Validated
    }
  }

  // it tries to match an expression between parentheses of ONLY 1 nested level
  // TODO keep counts of other open parentheses to parse multiple nesting level e.g. context (index, line, nestedCount)
  private fun grouping(tokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> {
    fun loop(tmp: List<Token>, expressionTokens: List<Token>): Pair<List<Token>, FreeB<Predicate>> =
      when {
        tmp.isEmpty() ->
          error("expected ')' after expression") // TODO Validated
        tmp.first() is Token.RightParentheses ->
          tmp.drop(1) to FreeB.Grouping(expression(expressionTokens))
        else ->
          loop(tmp.drop(1), expressionTokens + tmp.first())
      }
    return loop(tokens, emptyList())
  }
}

fun main() {
  println(Parser.parse(StringLexer.tokenize("8 < 42 == 6 > 3 AND 4 < 9")).pretty())
}
