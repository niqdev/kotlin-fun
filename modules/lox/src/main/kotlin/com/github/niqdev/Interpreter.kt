package com.github.niqdev

class LoxRuntimeError(val token: Token, message: String) : RuntimeException(message)

// >>> (7)

// tree-walk interpreter
class Interpreter {

  private val globals = Environment()
  private var environment = globals

  init {
    // "Lisp-1" means functions and variables occupy the same namespace
    globals.define("time", LoxCallable.nativeTime)
    globals.define("printLine", LoxCallable.nativePrintLine)
  }

  fun interpret(statements: List<Stmt>): Unit =
    try {
      statements.forEach(::execute)
    } catch (e: LoxRuntimeError) {
      Lox.reportRuntimeError(e)
    }

  private fun execute(statement: Stmt): Any? =
    when (statement) {
      is Stmt.Block -> executeBlock(statement)
      is Stmt.Expression -> evaluate(statement.expression)
      is Stmt.Function -> executeFunction(statement)
      is Stmt.If -> executeIf(statement)
      is Stmt.Print -> executePrint(statement)
      is Stmt.Return -> executeReturn(statement)
      is Stmt.Var -> executeVar(statement)
      is Stmt.While -> executeWhile(statement)
      is Stmt.Empty -> println("TODO no statement")
    }

  private fun executeBlock(statement: Stmt.Block): Unit =
    executeBlock(statement.statements, Environment(environment))

  fun executeBlock(statements: List<Stmt>, environment: Environment) {
    val previous = this.environment
    try {
      // current innermost scope
      this.environment = environment
      for (statement in statements) {
        execute(statement)
      }
    } finally {
      this.environment = previous
    }
  }

  private fun executeFunction(statement: Stmt.Function): Unit =
    environment.define(statement.name.lexeme, LoxFunction(statement, environment))

  private fun executeIf(statement: Stmt.If): Any? =
    if (isTruthy(evaluate(statement.condition))) execute(statement.thenBranch)
    else execute(statement.elseBranch)

  private fun executePrint(statement: Stmt.Print): Any? =
    LoxCallable.nativePrintLine.call(this, listOf(evaluate(statement.expression)))

  private fun executeReturn(statement: Stmt.Return): ReturnFunction {
    val value =
      if (statement.value is Expr.Empty) Stmt.Empty
      else evaluate(statement.value)

    // ??? using exception class for control flow and not actual error handling
    // When we execute a return statement, we'll use an exception to unwind the interpreter
    // past the visit methods of all of the containing statements back to the code that began executing the body
    throw ReturnFunction(value)
  }
  class ReturnFunction(val value: Any?) : RuntimeException(null, null, false, false)

  private fun executeVar(statement: Stmt.Var): Unit =
    environment.define(statement.name.lexeme, evaluate(statement.initializer))

  // returns unit
  private fun executeWhile(statement: Stmt.While) {
    while (isTruthy(evaluate(statement.condition))) {
      execute(statement.body)
    }
  }

  private fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Assign -> evaluateAssign(expression)
      is Expr.Binary -> evaluateBinary(expression)
      is Expr.Call -> evaluateCall(expression)
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Literal -> expression.value
      is Expr.Logical -> evaluateLogical(expression)
      is Expr.Unary -> evaluateUnary(expression)
      is Expr.Variable -> evaluateVariable(expression)
      is Expr.Empty -> println("TODO no expression")
    }

  private fun evaluateAssign(expression: Expr.Assign): Any =
    environment.assign(expression.name, evaluate(expression.value))

  private fun evaluateVariable(expression: Expr.Variable): Any? =
    environment.get(expression.name)

  private fun evaluateCall(expression: Expr.Call): Any? {
    val callee = evaluate(expression.callee)

    val arguments = expression.arguments.fold(listOf<Any?>()) { list, argument ->
      list + evaluate(argument)
    }

    val function = when {
      callee !is LoxCallable -> LoxRuntimeError(expression.paren, "Can only call functions and classes")
      arguments.size != callee.arity() -> LoxRuntimeError(
        expression.paren,
        "Expected ${callee.arity()} arguments but got ${arguments.size}"
      )
      else -> callee.call(this, arguments)
    }

    return function
  }

  private fun evaluateLogical(expression: Expr.Logical): Any? {
    val left = evaluate(expression.left)

    if (expression.op.type == TokenType.OR) {
      if (isTruthy(left)) return left
    } else {
      if (!isTruthy(left)) return left
    }

    return evaluate(expression.right)
  }

  private fun evaluateBinary(expression: Expr.Binary): Any {
    val left = evaluate(expression.left)
    val right = evaluate(expression.right)
    return when (expression.op.type) {
      TokenType.MINUS -> {
        checkNumberOperand(expression.op, right)
        left as Double - right as Double
      }
      TokenType.SLASH -> {
        checkNumberOperands(expression.op, left, right)
        left as Double / right as Double
      }
      TokenType.STAR -> {
        checkNumberOperands(expression.op, left, right)
        left as Double * right as Double
      }
      TokenType.GREATER -> {
        checkNumberOperands(expression.op, left, right)
        left as Double > right as Double
      }
      TokenType.GREATER_EQUAL -> {
        checkNumberOperands(expression.op, left, right)
        left as Double >= right as Double
      }
      TokenType.LESS -> {
        checkNumberOperands(expression.op, left, right)
        (left as Double) < right as Double
      }
      TokenType.LESS_EQUAL -> {
        checkNumberOperands(expression.op, left, right)
        left as Double <= right as Double
      }
      TokenType.BANG_EQUAL -> !isEqual(left, right)
      TokenType.EQUAL_EQUAL -> isEqual(left, right)
      TokenType.PLUS ->
        when {
          left is Double && right is Double -> left + right
          left is String && right is String -> "$left$right"
          else -> throw LoxRuntimeError(expression.op, "Unsupported binary operation")
        }
      else -> throw LoxRuntimeError(expression.op, "Unsupported binary expression")
    }
  }

  private fun isEqual(left: Any?, right: Any?): Boolean =
    (left == null && right == null) || left == right

  private fun evaluateUnary(expression: Expr.Unary): Any {
    val right = evaluate(expression.right)
    return when (expression.op.type) {
      TokenType.MINUS ->
        when (right) {
          is Double -> -right
          else -> throw LoxRuntimeError(expression.op, "Expected number")
        }
      TokenType.BANG ->
        when (right) {
          is Double -> !isTruthy(right)
          else -> throw LoxRuntimeError(expression.op, "Expected number")
        }
      else -> throw LoxRuntimeError(expression.op, "Unsupported unary expression")
    }
  }

  // Ruby's simple rule: false and nil are falsey, and everything else is truthy
  private fun isTruthy(any: Any?): Boolean =
    when (any) {
      (any == null) -> false
      is Boolean -> any
      else -> true
    }

  private fun checkNumberOperand(operator: Token, operand: Any?) {
    if (operand is Double) return
    throw LoxRuntimeError(operator, "Operand must be a number")
  }

  private fun checkNumberOperands(operator: Token, left: Any?, right: Any?) {
    if (left is Double && right is Double) return
    throw LoxRuntimeError(operator, "Operands must be numbers")
  }

  companion object {
    fun stringify(obj: Any?): String =
      when (obj) {
        null -> "nil"
        is Double -> {
          val text = obj.toString()
          if (text.endsWith(".0")) {
            text.substring(0, text.length - 2)
          }
          text
        }
        else -> obj.toString()
      }
  }
}
