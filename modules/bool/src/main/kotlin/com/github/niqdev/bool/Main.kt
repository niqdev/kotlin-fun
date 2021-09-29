package com.github.niqdev.bool

fun main() {
  val tokens = StringLexer.tokenize("8 < 42")
  println(tokens.map { Token.pretty(it) })

  // val expression = Parser.parse(tokens)
  // println(Expression.pretty(expression))

//  val result = Interpreter.interpret(expression)
//  println("RESULT: $result")
}
