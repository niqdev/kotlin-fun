package com.github.niqdev.bool

// lexer: string -> List<Token>
// parser: List<Token> -> FreeB<Predicate>
// interpreter: FreeB<Predicate> -> Boolean

fun main() {
  val tokens = StringLexer.tokenize("(8 > 42 AND !6 < 3) OR !!!1==2")
  println(tokens.map { Token.pretty(it) })
  val predicates = Parser.parse(tokens)
  println(predicates.pretty())
  val result = predicates.run()
  println("bool: $result")
}
