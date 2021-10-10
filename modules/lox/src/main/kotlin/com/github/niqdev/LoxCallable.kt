package com.github.niqdev

// >>> (9)

interface LoxCallable {
  // arity: number of arguments a function or operation expects i.e. number of parameters it declares
  fun arity(): Int
  fun call(interpreter: Interpreter, arguments: List<Any?>): Any?

  companion object {
    val nativeTime = object : LoxCallable {
      override fun arity(): Int = 0
      override fun call(interpreter: Interpreter, arguments: List<Any?>): Any =
        System.currentTimeMillis().toDouble() / 1000.0
      override fun toString(): String = "<native fn>"
    }

    val nativePrintLine = object : LoxCallable {
      override fun arity(): Int = 1
      override fun call(interpreter: Interpreter, arguments: List<Any?>): Any =
        println(Interpreter.stringify(arguments[0]))
      override fun toString(): String = "<native fn>"
    }
  }
}

class LoxFunction(private val declaration: Stmt.Function) : LoxCallable {

  override fun arity(): Int = declaration.params.size

  // - a function encapsulates its parameters: each function gets its own environment where it stores those variables
  // - environment must be created dynamically: each function call gets its own environment
  override fun call(interpreter: Interpreter, arguments: List<Any?>): Any? {
    val environment = Environment(interpreter.globals)
    declaration.params.forEachIndexed { index, token ->
      environment.define(token.lexeme, arguments[index])
    }
    try {
      interpreter.executeBlock(declaration.body, environment)
    } catch (returnValue: Interpreter.ReturnFunction) {
      return returnValue.value
    }
    // body without return statement
    return null
  }

  override fun toString(): String = "<fn ${declaration.name.lexeme}>"
}
