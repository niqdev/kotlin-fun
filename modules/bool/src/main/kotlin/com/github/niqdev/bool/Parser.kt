package com.github.niqdev.bool

import com.github.niqdev.bool.internal.InternalList

// build Abstract Syntax Tree
object Parser {

  // TODO Validated<NonEmptyList<Error>, Expression>
  fun parse(tokens: InternalList<Token>): Expression =
    when (tokens) {
      is InternalList.Nil -> TODO()
      is InternalList.Cons -> TODO()
    }
}
