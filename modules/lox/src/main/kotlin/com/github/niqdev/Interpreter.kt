package com.github.niqdev

class LoxRuntimeError(val token: Token, message: String) : RuntimeException(message)

// tree-walk interpreter
class Interpreter {

  fun interpret(statements: List<Stmt>): Unit =
    try {
      statements.forEach(::execute)
    } catch (e: LoxRuntimeError) {
      Lox.reportRuntimeError(e)
    }

  private fun execute(statement: Stmt): Any? =
    when (statement) {
      is Stmt.Expression -> {
        val expression = statement.expression
        println("EXPR: ${expression.pretty()}")
        val value = evaluate(expression)
        println("RESULT: ${stringify(value)}")
      }
      is Stmt.Print -> println(stringify(evaluate(statement.expression)))
      is Stmt.Var -> TODO()
      is Stmt.Empty -> TODO()
    }

  private fun evaluate(expression: Expr): Any? =
    when (expression) {
      is Expr.Binary -> evaluateBinary(expression)
      is Expr.Grouping -> evaluate(expression.expression)
      is Expr.Literal -> expression.value
      is Expr.Unary -> evaluateUnary(expression)
      is Expr.Variable -> TODO()
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
          else -> throw LoxRuntimeError(expression.op, "Unsupported binary operation") // TODO
        }
      else -> throw LoxRuntimeError(expression.op, "Unsupported binary expression") // TODO
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
          else -> throw LoxRuntimeError(expression.op, "Expected number") // TODO
        }
      TokenType.BANG ->
        when (right) {
          is Double -> !isTruthy(right)
          else -> throw LoxRuntimeError(expression.op, "Expected number") // TODO
        }
      else -> throw LoxRuntimeError(expression.op, "Unsupported unary expression") // TODO
    }
  }

  // Ruby's simple rule: false and nil are falsey, and everything else is truthy
  private fun isTruthy(any: Any?): Boolean =
    when (any) {
      any == null -> false
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

  private fun stringify(obj: Any?): String =
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
