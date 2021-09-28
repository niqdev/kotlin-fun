package com.github.niqdev.bool

import com.github.niqdev.bool.internal.InternalList
import com.github.niqdev.bool.internal.cons
import com.github.niqdev.bool.internal.reverse

object StringLexer {

  // TODO Validated<NonEmptyList<Error>, List<Token>>
  fun tokenize(input: String): InternalList<Token> {

    tailrec fun loop(index: Int, result: InternalList<Token>): InternalList<Token> =
      when {
        index < input.length -> {
          when (val c = input[index]) {
            '(' -> loop(index + 1, result.cons()(Token.TokenLeftParentheses))
            ')' -> loop(index + 1, result.cons()(Token.TokenRightParentheses))

            // TODO 2 chars
            '=' -> loop(index + 1, result.cons()(Token.TokenEqual))
            '<' -> loop(index + 1, result.cons()(Token.TokenLess))
            '>' -> loop(index + 1, result.cons()(Token.TokenGreater))

            // TODO alias
            '&' -> TODO()
            '|' -> TODO()
            '!' -> TODO()

            '"' -> TODO()

            // ignore
            ' ', '\t', '\n', '\r', '\b' -> loop(index + 1, result)
            else -> {
              when {
                c.isDigit() -> {
                  val tokenString = scanNumber()(input.substring(index))
                  loop(index + tokenString.length, result.cons()(Token.TokenInt(tokenString.toInt())))
                }
                // TODO key instead of string
                c.isAlpha() -> {
                  val tokenString = scanString()(input.substring(index))
                  loop(index + tokenString.length, result.cons()(Token.TokenString(tokenString)))
                }
                // TODO and, or, not
                else -> error("invalid char $c at position $index")
              }
            }
          }
        }
        else -> result.reverse()
      }

    return loop(0, InternalList.Nil)
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
