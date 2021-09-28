package com.github.niqdev.bool

import com.github.niqdev.bool.internal.map

fun main() {
  println(StringLexer.tokenize("( ) = < > 42 dho").map { Token.pretty(it) })

//  val tokens = StringLexer.tokenize("8 < 42")
//  val expression = Parser.parse(tokens)
//  val result = Interpreter.interpret(expression)
//  println("RESULT: $result")
}
