package com.github.niqdev

// TODO State Monad ?
// variables are lexically scoped
// when a local variable has the same name as a variable in an enclosing scope, it shadows the outer one
// lexical scope is a specific style of scoping where the text of the program itself shows where a scope begins and ends e.g. using curly brackets
class Environment(private val enclosing: Environment? = null) {

  private val values = mutableMapOf<String, Any?>()

  // a new variable is always declared in the current innermost scope
  fun define(name: String, value: Any?) {
    values[name] = value
  }

  // `define` only if it exists already
  fun assign(name: Token, value: Any?): Unit =
    when {
      // verify inner scope first
      values.contains(name.lexeme) -> define(name.lexeme, value)
      // and outer scope next
      enclosing != null -> enclosing.assign(name, value)
      else -> throw LoxRuntimeError(name, "Undefined variable: ${name.lexeme}")
    }

  fun get(name: Token): Any? =
    when {
      // verify inner scope first
      values.contains(name.lexeme) -> values[name.lexeme]
      // and outer scope next
      enclosing != null -> enclosing.get(name)
      else -> throw LoxRuntimeError(name, "Undefined variable: ${name.lexeme}")
    }
}
