package com.github.niqdev.bool

// TODO "IN a, b, b" and "MATCH /aaa/"
sealed class Token {
  object TokenLeftParentheses : Token()
  object TokenRightParentheses : Token()
  object TokenMinus : Token()
  object TokenBangEqual : Token()
  object TokenEqual : Token()
  object TokenEqualEqual : Token()
  object TokenGreater : Token()
  object TokenGreaterEqual : Token()
  object TokenLess : Token()
  object TokenLessEqual : Token()
  object TokenTrue : Token()
  object TokenFalse : Token()
  object TokenAnd : Token()
  object TokenOr : Token()
  object TokenNot : Token()
  data class TokenNumber(val value: Int) : Token()
  data class TokenString(val value: String) : Token()
  data class TokenKey(val value: String) : Token()

  companion object {
    val identifiers = mapOf(
      "TRUE" to TokenTrue,
      "FALSE" to TokenFalse,
      "AND" to TokenAnd,
      "OR" to TokenOr,
      "NOT" to TokenNot
    )

    fun pretty(token: Token) =
      when (token) {
        is TokenLeftParentheses -> "("
        is TokenRightParentheses -> ")"
        is TokenMinus -> "-"
        is TokenBangEqual -> "!="
        is TokenEqual -> "="
        is TokenEqualEqual -> "=="
        is TokenGreater -> ">"
        is TokenGreaterEqual -> ">="
        is TokenLess -> "<"
        is TokenLessEqual -> "<="
        is TokenTrue -> "TRUE"
        is TokenFalse -> "FALSE"
        is TokenAnd -> "AND"
        is TokenOr -> "OR"
        is TokenNot -> "NOT"
        is TokenNumber -> "Number(${token.value})"
        is TokenString -> "String(${token.value})"
        is TokenKey -> "Key(${token.value})"
      }
  }
}
