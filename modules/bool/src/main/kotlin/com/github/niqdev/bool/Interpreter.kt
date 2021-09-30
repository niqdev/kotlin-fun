package com.github.niqdev.bool

// TODO review free boolean algebra
object Interpreter {

  // TODO Validated<NonEmptyList<Error>, Boolean>
  fun evaluateBoolean(expression: Expression): Boolean =
    when (expression) {
      is Expression.Binary -> evaluateBinaryBoolean(expression)
      is Expression.Grouping -> TODO()
      is Expression.Literal -> evaluateLiteralBoolean(expression)
      is Expression.Unary -> error("invalid Boolean expression") // TODO Validated
    }

  fun evaluate(expression: Expression): Expression =
    when (expression) {
      is Expression.Binary -> TODO()
      is Expression.Grouping -> TODO()
      is Expression.Literal -> TODO()
      is Expression.Unary -> error("invalid Boolean expression") // TODO Validated
    }

  private fun evaluateBinaryBoolean(expression: Expression.Binary): Boolean =
    if (expression.left is Expression.Unary && expression.right is Expression.Unary) {
      val left = evaluateUnary(expression.left)
      val right = evaluateUnary(expression.right)
      when (expression.token) {
        is Token.TokenGreater ->
          if (left.value is Value.NumberValue && right.value is Value.NumberValue) {
            left.value.number > right.value.number
          } else {
            error("invalid binary expression") // TODO Validated
          }
        else -> error("invalid binary expression") // TODO Validated
      }
    } else {
      error("invalid binary expression") // TODO Validated
    }

  private fun evaluateUnary(expression: Expression.Unary): Expression.Literal =
    when (expression.token) {
      is Token.TokenNot ->
        when (val value = expression.literal.value) {
          is Value.TrueValue -> Expression.Literal(Value.FalseValue)
          is Value.FalseValue -> Expression.Literal(Value.TrueValue)
          else -> error("invalid unary expression: [${Token.pretty(Token.TokenNot)}][$value]") // TODO Validated
        }
      is Token.TokenMinus ->
        when (val value = expression.literal.value) {
          is Value.NumberValue -> Expression.Literal(Value.NumberValue(value.number))
          else -> error("invalid unary expression: [${Token.pretty(Token.TokenMinus)}][$value]") // TODO Validated
        }
      else -> error("invalid unary expression") // TODO Validated
    }

  private fun evaluateLiteralBoolean(expression: Expression.Literal): Boolean =
    when (expression.value) {
      is Value.TrueValue -> true
      is Value.FalseValue -> false
      else -> error("invalid boolean expression") // TODO Validated
    }
}
