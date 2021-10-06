package com.github.niqdev.bool

// lexer: string -> List<Token>
// parser: List<Token> -> FreeB<Predicate>
// interpreter: FreeB<Predicate> -> Boolean

fun main() {
  // e.g. json path
  val keys = mapOf("my.number-key" to Token.Number(42))

  val tokens = StringLexer.tokenize("(8 > my.number-key AND !6 < 3) OR !!!1==2")
  println(tokens.map { Token.pretty(it) })

  val predicates = Parser.parse(tokens, keys)
  println(predicates.pretty())

  val result = predicates.run()
  println("bool: $result")
}
