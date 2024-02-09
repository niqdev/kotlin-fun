package com.github.niqdev.query

sealed interface Operator {
  data object Eq : Operator
  data object Neq : Operator
  data object Lt : Operator
  data object Lte : Operator
  data object Gt : Operator
  data object Gte : Operator
}

fun Operator.pretty(): String =
  when (this) {
    is Operator.Eq -> "=="
    is Operator.Neq -> "!="
    is Operator.Lt -> "<"
    is Operator.Lte -> "<="
    is Operator.Gt -> ">"
    is Operator.Gte -> ">="
  }
