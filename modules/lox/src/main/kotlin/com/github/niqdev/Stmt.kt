package com.github.niqdev

sealed interface Stmt {
  data class Block(val statements: List<Stmt>) : Stmt
  data class Expression(val expression: Expr) : Stmt
  data class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt) : Stmt
  data class Print(val expression: Expr) : Stmt
  data class Var(val name: Token, val initializer: Expr) : Stmt
  object Empty : Stmt
}
