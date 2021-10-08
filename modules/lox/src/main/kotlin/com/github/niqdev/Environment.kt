package com.github.niqdev

// TODO State Monad ?
class Environment {
  private val values = mutableMapOf<String, Any?>()

  fun define(name: String, value: Any?): Unit {
    values[name] = value
  }

  fun get(name: Token): Any? =
    if (values.contains(name.lexeme)) values[name.lexeme]
    else throw LoxRuntimeError(name, "Undefined variable: ${name.lexeme}")

}
