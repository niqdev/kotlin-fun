package com.github.niqdev

// >>> (4)

/**
 * The grammar is a recursive data structure that forms a tree: abstract syntax tree (AST)
 *
 * Complete grammar: https://craftinginterpreters.com/appendix-i.html
 *
 * // simple but ambiguous
 * expression     → literal | unary | binary | grouping ;
 * literal        → NUMBER | STRING | "true" | "false" | "nil" ;
 * grouping       → "(" expression ")" ;
 * unary          → ( "-" | "!" ) expression ;
 * binary         → expression operator expression ;
 * operator       → "==" | "!=" | "<" | "<=" | ">" | ">=" | "+"  | "-"  | "*" | "/" ;
 *
 * // no ambiguity
 * expression     → equality ;
 * equality       → comparison ( ( "!=" | "==" ) comparison )* ;
 * comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
 * term           → factor ( ( "-" | "+" ) factor )* ;
 * factor         → unary ( ( "/" | "*" ) unary )* ; // rule recurse to match the left operand: left-associative
 * unary          → ( "!" | "-" ) unary | primary ;
 * primary        → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
 */
sealed interface Expr {
  data class Assign(val name: Token, val value: Expr) : Expr
  data class Binary(val left: Expr, val op: Token, val right: Expr) : Expr
  data class Call(val callee: Expr, val paren: Token, val arguments: List<Expr>) : Expr
  data class Grouping(val expression: Expr) : Expr

  // TODO use Value instead of Any?
  data class Literal(val value: Any?) : Expr
  data class Logical(val left: Expr, val op: Token, val right: Expr) : Expr
  data class Unary(val op: Token, val right: Expr) : Expr
  data class Variable(val name: Token) : Expr
  object Empty : Expr
}

fun Expr.pretty(): String =
  when (this) {
    is Expr.Assign -> "(${name.pretty()} ${value.pretty()})"
    is Expr.Binary -> "(${left.pretty()} ${op.lexeme} ${right.pretty()})"
    is Expr.Call -> "TODO $this"
    is Expr.Grouping -> "(${expression.pretty()})"
    is Expr.Literal -> value.toString()
    is Expr.Logical -> "(${left.pretty()} ${op.lexeme} ${right.pretty()})"
    is Expr.Unary -> "(${op.lexeme}${right.pretty()})"
    is Expr.Variable -> name.pretty()
    is Expr.Empty -> "EMPTY"
  }

sealed interface Value {
  object True : Value
  object False : Value
  object Null : Value
  data class String(val string: kotlin.String) : Value
  data class Number(val double: Double) : Value
}

fun Value.pretty(): String =
  when (this) {
    is Value.True -> "true"
    is Value.False -> "false"
    is Value.Null -> "null"
    is Value.String -> "\"$string\""
    is Value.Number -> "$double"
  }

fun main(args: Array<String>) {
  val expr =
    Expr.Binary(
      Expr.Unary(Token(TokenType.MINUS, "-", null, 1), Expr.Literal(123)),
      Token(TokenType.STAR, "*", null, 1),
      Expr.Literal(45.67)
    )

  // ((-123.0) * 45.67)
  println(expr.pretty())
}
