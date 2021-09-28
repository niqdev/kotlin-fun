package com.github.niqdev.bool

import com.github.niqdev.bool.internal.map

fun main() {
  val example = "( ) != = == > >= < <= AND and && OR oR || NOT NOt ! 42 simple_key key.sub-nested \"a str@ing\" 8"
  println(StringLexer.tokenize(example).map { Token.pretty(it) })

  val tokens = StringLexer.tokenize("8 < 42")
  val expression = Parser.parse(tokens)
//  val result = Interpreter.interpret(expression)
//  println("RESULT: $result")
}
