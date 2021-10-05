package com.github.niqdev.bool

// TODO alternative: Token(tokenType, index, line)
sealed interface Token {
  object LeftParentheses : Token
  object RightParentheses : Token
  object BangEqual : Token
  object Equal : Token
  object EqualEqual : Token
  object Greater : Token
  object GreaterEqual : Token
  object Less : Token
  object LessEqual : Token
  object True : Token
  object False : Token
  object And : Token
  object Or : Token
  object Not : Token
  object In : Token
  object Match : Token
  data class Number(val int: Int) : Token
  data class String(val string: kotlin.String) : Token
  data class TokenKey(val key: kotlin.String) : Token

  companion object {
    val identifiers = mapOf(
      "TRUE" to True,
      "FALSE" to False,
      "AND" to And,
      "OR" to Or,
      "NOT" to Not,
      "IN" to In,
      "MATCH" to Match
    )

    fun pretty(token: Token) =
      when (token) {
        is LeftParentheses -> "("
        is RightParentheses -> ")"
        is BangEqual -> "!="
        is Equal -> "="
        is EqualEqual -> "=="
        is Greater -> ">"
        is GreaterEqual -> ">="
        is Less -> "<"
        is LessEqual -> "<="
        is True -> "TRUE"
        is False -> "FALSE"
        is And -> "AND"
        is Or -> "OR"
        is Not -> "NOT"
        is In -> "IN"
        is Match -> "MATCH"
        is Number -> "Number(${token.int})"
        is String -> "String(${token.string})"
        is TokenKey -> "Key(${token.key})"
      }
  }
}
