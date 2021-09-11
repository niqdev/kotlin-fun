package com.github.niqdev

/**
 * example: 1 - (2 * 3) < 4 == false
 *
 * expression     → literal | unary | binary | grouping ;
 * literal        → NUMBER | STRING | "true" | "false" | "nil" ;
 * grouping       → "(" expression ")" ;
 * unary          → ( "-" | "!" ) expression ;
 * binary         → expression operator expression ;
 * operator       → "==" | "!=" | "<" | "<=" | ">" | ">=" | "+"  | "-"  | "*" | "/" ;
 *
 * Since the grammar is recursive the data structure will form a tree: abstract syntax tree (AST)
 */
sealed class Expr {
  class Binary(val left: Expr, val op: Token, val right: Expr) : Expr()
  class Grouping(val expression: Expr) : Expr()
  class Literal(val value: Any) : Expr()
  class Unary(val op: Token, val expression: Expr) : Expr()

  companion object {
    // pretty abstract syntax tree
    fun pretty(expr: Expr): String =
      when (expr) {
        is Binary -> parenthesize(expr.op.lexeme, expr.left, expr.right)
        is Grouping -> parenthesize("group", expr.expression)
        is Literal -> expr.value.toString()
        is Unary -> parenthesize(expr.op.lexeme, expr.expression)
      }

    private fun parenthesize(name: String, vararg exprs: Expr): String {
      val values = exprs.fold("", { result, expr -> "$result ${pretty(expr)}" })
      return "($name$values)"
    }

    @JvmStatic
    fun main(args: Array<String>) {
      val expr =
        Binary(
          Unary(Token(TokenType.MINUS, "-", null, 1), Literal(123)),
          Token(TokenType.STAR, "*", null, 1),
          Grouping(Literal(45.67))
        )

      // (* (- 123) (group 45.67))
      println(pretty(expr))
    }
  }
}
