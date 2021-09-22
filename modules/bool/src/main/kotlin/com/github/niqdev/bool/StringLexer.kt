package com.github.niqdev.bool

sealed class Token
data class MyInt(val int: Int) : Token()
data class MyString(val string: String) : Token()
object MyAnd : Token()

object StringLexer {

  fun tokenize(value: String): List<Token> {

    fun loop(start: Int, current: Int, result: List<Token>): List<Token> =
      when (value[start]) {
        else -> TODO()
      }

    return loop(0, 0, listOf())
  }
}
