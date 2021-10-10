package com.github.niqdev

// >>> (6)

sealed interface Stmt {
  data class Block(val statements: List<Stmt>) : Stmt
  data class Expression(val expression: Expr) : Stmt
  data class Function(val name: Token, val params: List<Token>, val body: List<Stmt>) : Stmt
  data class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt) : Stmt
  data class Print(val expression: Expr) : Stmt
  data class Return(val keyword: Token, val value: Expr) : Stmt
  data class Var(val name: Token, val initializer: Expr) : Stmt
  data class While(val condition: Expr, val body: Stmt) : Stmt
  object Empty : Stmt
}
