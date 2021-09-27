package com.github.niqdev

sealed class Stmt {
  class Expression(val expression: Expr) : Stmt()
  class Print(val expression: Expr) : Stmt()
}
