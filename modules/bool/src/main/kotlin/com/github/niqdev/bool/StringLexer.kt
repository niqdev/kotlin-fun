package com.github.niqdev.bool

enum class TokenType {
  // single-character tokens
  LEFT_PAREN,
  RIGHT_PAREN,

  // one or two character tokens
  BANG,
  BANG_EQUAL,
  EQUAL_EQUAL,
  GREATER,
  GREATER_EQUAL,
  LESS,
  LESS_EQUAL,

  // literals
  STRING,
  NUMBER,

  // keywords
  AND,
  OR
}

sealed class Token {
  internal data class TokenInt(val value: Int) : Token()
  internal data class TokenString(val value: String) : Token()
  internal object TokenLess : Token()
}

fun Token.pretty() =
  when (this) {
    is Token.TokenInt -> "Int($value)"
    is Token.TokenString -> "String($value)"
    is Token.TokenLess -> "Symbol(<)"
  }

object StringLexer {

  // TODO Validated<NonEmptyList<Error>, List<Token>>
  fun tokenize(input: String): List<Token> {

    tailrec fun loop(index: Int, result: List<Token>): List<Token> =
      when {
        index < input.length -> {
          when (val c = input[index]) {
            '(' -> TODO()
            ')' -> TODO()
            '!' -> TODO()
            '=' -> TODO()
            '<' -> {
              val token = Token.TokenLess
              loop(index + 1, result + token)
            }
            '>' -> TODO()
            '&' -> TODO()
            '|' -> TODO()
            '"' -> TODO()

            ' ', '\t', '\n', '\r', '\b' ->
              loop(index + 1, result)
            else -> {
              when {
                c.isDigit() -> {
                  val tokenString = scanNumber()(input.substring(index))
                  loop(index + tokenString.length, result + Token.TokenInt(tokenString.toInt()))
                }
                c.isAlpha() -> {
                  val tokenString = scanString()(input.substring(index))
                  loop(index + tokenString.length, result + Token.TokenString(tokenString))
                }
                else -> error("invalid char $c at position $index")
              }
            }
          }
        }
        else -> result
      }

    return loop(0, listOf())
  }

  fun scan(): ((Char) -> Boolean) -> (String) -> String =
    { predicate ->
      { input ->

        tailrec fun loop(index: Int, result: String): String =
          when {
            // end of the input string
            index >= input.length -> result
            // append valid char
            predicate(input[index]) -> loop(index + 1, result + input[index])
            else -> result
          }

        loop(0, "")
      }
    }

  fun scanNumber(): (String) -> String = scan()(Char::isDigit)
  fun scanString(): (String) -> String = scan()(Char::isAlpha)
}

private fun Char.isDigit(): Boolean = this in '0'..'9'
private fun Char.isAlpha(): Boolean = this in 'a'..'z' || this in 'A'..'Z' || "._-".contains(this)

fun main() {
  println(StringLexer.tokenize("8 < 42 aaa.bbb 56").map { it.pretty() })
}
