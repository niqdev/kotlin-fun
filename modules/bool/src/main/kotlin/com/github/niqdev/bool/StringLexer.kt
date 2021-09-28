package com.github.niqdev.bool

import com.github.niqdev.bool.internal.InternalList
import com.github.niqdev.bool.internal.plus
import com.github.niqdev.bool.internal.reverse

object StringLexer {

  // TODO Validated<NonEmptyList<Error>, List<Token>>
  fun tokenize(input: String): InternalList<Token> {

    tailrec fun loop(index: Int, result: InternalList<Token>): InternalList<Token> =
      when {
        index < input.length -> {
          when (val c = input[index]) {
            // parentheses
            '(' -> loop(index + 1, result + Token.TokenLeftParentheses)
            ')' -> loop(index + 1, result + Token.TokenRightParentheses)

            // comparison
            '=' ->
              when (input[index + 1]) {
                '=' -> loop(index + 2, result + Token.TokenEqualEqual)
                else -> loop(index + 1, result + Token.TokenEqual)
              }
            '<' ->
              when (input[index + 1]) {
                '=' -> loop(index + 2, result + Token.TokenLessEqual)
                else -> loop(index + 1, result + Token.TokenLess)
              }
            '>' ->
              when (input[index + 1]) {
                '=' -> loop(index + 2, result + Token.TokenGreaterEqual)
                else -> loop(index + 1, result + Token.TokenGreater)
              }

            // aliases
            '&' ->
              when (input[index + 1]) {
                '&' -> loop(index + 2, result + Token.TokenAnd)
                else -> error("invalid char $c at position $index") // TODO Validated
              }
            '|' ->
              when (input[index + 1]) {
                '|' -> loop(index + 2, result + Token.TokenOr)
                else -> error("invalid char $c at position $index") // TODO Validated
              }
            '!' ->
              when (input[index + 1]) {
                '=' -> loop(index + 2, result + Token.TokenBangEqual)
                else -> loop(index + 1, result + Token.TokenNot)
              }

            '"' -> {
              val tokenString = scanString()(input.substring(index + 1))
              // skip 2 quotes
              loop(index + tokenString.length + 2, result + Token.TokenString(tokenString))
            }

            // ignore
            ' ', '\t', '\n', '\r', '\b' -> loop(index + 1, result)
            else -> {
              when {
                c.isDigit() -> {
                  val tokenString = scanNumber()(input.substring(index))
                  // safe: no NumberFormatException
                  loop(index + tokenString.length, result + Token.TokenInt(tokenString.toInt()))
                }
                // identifier or key (cannot start with "._-")
                c in 'a'..'z' || c in 'A'..'Z' -> {
                  val tokenString = scanKey()(input.substring(index))
                  // identifier is case insensitive
                  val token = Token.identifiers.getOrDefault(tokenString.toUpperCase(), Token.TokenKey(tokenString))
                  loop(index + tokenString.length, result + token)
                }
                else -> error("invalid char $c at position $index") // TODO Validated
              }
            }
          }
        }
        else -> result.reverse()
      }

    return loop(0, InternalList.Nil)
  }

  private fun scan(): ((Char) -> Boolean) -> (String) -> String =
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

  private fun scanString(): (String) -> String = scan()() { it != '"' }
  private fun scanNumber(): (String) -> String = scan()(Char::isDigit)
  private fun scanKey(): (String) -> String = scan()(Char::isKey)
}

private fun Char.isDigit(): Boolean = this in '0'..'9'
private fun Char.isKey(): Boolean = this in 'a'..'z' || this in 'A'..'Z' || "._-".contains(this)
