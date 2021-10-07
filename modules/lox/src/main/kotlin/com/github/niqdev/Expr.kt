package com.github.niqdev

// >>> (4)

/**
 * example: 1 - (2 * 3) < 4 == false
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
 *
 * // add statements
 * program        → declaration* EOF ;
 * declaration    → varDecl
 *                  | statement ;
 * varDecl        → "var" IDENTIFIER ( "=" expression )? ";" ;
 * statement      → exprStmt
 *                  | printStmt ;
 * exprStmt       → expression ";" ;
 * printStmt      → "print" expression ";" ;
 * primary        → ... | IDENTIFIER ;
 *
 * the grammar is recursive the data structure forms a tree: abstract syntax tree (AST)
 */
sealed interface Expr {
  data class Binary(val left: Expr, val op: Token, val right: Expr) : Expr
  data class Grouping(val expression: Expr) : Expr
  data class Literal(val value: Any?) : Expr
  data class Unary(val op: Token, val right: Expr) : Expr
  data class Variable(val name: Token) : Expr
}

fun Expr.pretty(): String =
  when (this) {
    is Expr.Binary -> "(${left.pretty()} ${op.lexeme} ${right.pretty()})"
    is Expr.Grouping -> "(${expression.pretty()})"
    is Expr.Literal -> value.toString()
    is Expr.Unary -> "(${op.lexeme}${right.pretty()})"
    is Expr.Variable -> name.pretty()
  }

fun main(args: Array<String>) {
  val expr =
    Expr.Binary(
      Expr.Unary(Token(TokenType.MINUS, "-", null, 1), Expr.Literal(123)),
      Token(TokenType.STAR, "*", null, 1),
      Expr.Grouping(Expr.Literal(45.67))
    )

  // (* (- 123) (group 45.67))
  println(expr.pretty())
}
