package com.github.niqdev.bool

fun main() {
  val tokens = StringLexer.tokenize("8 < -42")
  println(tokens.map { Token.pretty(it) })

  // TODO fixme empty List
  // val expression = Parser.parse(tokens)
  // println(Expression.pretty(expression))

  // "8 > -42"
  val expression1 = Expression.Binary(
    Expression.Literal(Value.NumberValue(8)),
    Token.TokenGreater,
    Expression.Unary(Token.TokenMinus, Expression.Literal(Value.NumberValue(42)))
  )
  println(Expression.pretty(expression1))
  // "8 < 42 AND 6 > 3"
  val expression2 = Expression.Binary(
    Expression.Binary(
      Expression.Literal(Value.NumberValue(8)),
      Token.TokenLess,
      Expression.Literal(Value.NumberValue(42))
    ),
    Token.TokenAnd,
    Expression.Binary(
      Expression.Literal(Value.NumberValue(6)),
      Token.TokenGreater,
      Expression.Literal(Value.NumberValue(3))
    )
  )
  println(Expression.pretty(expression2))

  // val result = Interpreter.evaluate(expression1)
  // println("RESULT: $result")
}
