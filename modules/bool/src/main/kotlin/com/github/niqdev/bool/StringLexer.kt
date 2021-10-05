package com.github.niqdev.bool

object StringLexer {

  // TODO Validated<NonEmptyList<Error>, List<Token>>
  fun tokenize(input: String): List<Token> {

    tailrec fun loop(index: Int, result: List<Token>): List<Token> =
      when {
        index < input.length -> {
          when (val c = input[index]) {
            // parentheses
            '(' -> loop(index + 1, result + Token.LeftParentheses)
            ')' -> loop(index + 1, result + Token.RightParentheses)

            // comparison
            '=' ->
              when (input[index + 1]) {
                '=' -> loop(index + 2, result + Token.EqualEqual)
                else -> loop(index + 1, result + Token.Equal)
              }
            '<' ->
              when (input[index + 1]) {
                '=' -> loop(index + 2, result + Token.LessEqual)
                else -> loop(index + 1, result + Token.Less)
              }
            '>' ->
              when (input[index + 1]) {
                '=' -> loop(index + 2, result + Token.GreaterEqual)
                else -> loop(index + 1, result + Token.Greater)
              }

            // aliases
            '&' ->
              when (input[index + 1]) {
                '&' -> loop(index + 2, result + Token.And)
                else -> error("invalid char $c at position $index") // TODO Validated
              }
            '|' ->
              when (input[index + 1]) {
                '|' -> loop(index + 2, result + Token.Or)
                else -> error("invalid char $c at position $index") // TODO Validated
              }
            '!' ->
              when (input[index + 1]) {
                '=' -> loop(index + 2, result + Token.BangEqual)
                else -> loop(index + 1, result + Token.Not)
              }

            '"' -> {
              val tokenString = scanString()(input.substring(index + 1))
              // skip 2 quotes
              loop(index + tokenString.length + 2, result + Token.String(tokenString))
            }

            // ignore
            ' ', '\t', '\n', '\r', '\b' -> loop(index + 1, result)
            else -> {
              when {
                // negative (`-`) and decimal (`-` or `,`) numbers are not supported
                c.isDigit() -> {
                  val tokenString = scanNumber()(input.substring(index))
                  // safe: no NumberFormatException
                  loop(index + tokenString.length, result + Token.Number(tokenString.toInt()))
                }
                // identifier or key (cannot start with "._-")
                c in 'a'..'z' || c in 'A'..'Z' -> {
                  val tokenString = scanKey()(input.substring(index))
                  // identifier is case-insensitive
                  val token = Token.identifiers.getOrDefault(tokenString.uppercase(), Token.TokenKey(tokenString))
                  loop(index + tokenString.length, result + token)
                }
                else -> error("invalid char $c at position $index") // TODO Validated
              }
            }
          }
        }
        else -> result
      }

    return loop(0, listOf())
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

  // TODO validate max length
  private fun scanString(): (String) -> String = scan()() { it != '"' }
  // TODO validate Int.MAX_VALUE and Int.MIN_VALUE
  private fun scanNumber(): (String) -> String = scan()(Char::isDigit)
  // TODO validate max length
  private fun scanKey(): (String) -> String = scan()(Char::isKey)
}

private fun Char.isDigit(): Boolean = this in '0'..'9'
private fun Char.isKey(): Boolean = this in 'a'..'z' || this in 'A'..'Z' || "._-".contains(this)
