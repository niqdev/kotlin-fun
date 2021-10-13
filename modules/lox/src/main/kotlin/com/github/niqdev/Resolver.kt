package com.github.niqdev

// >>> (10)

// semantic analysis: where a parser tells only if a program is grammatically correct (a syntactic analysis),
// semantic analysis goes farther and starts to figure out what pieces of the program actually mean
// In this case, our analysis will resolve variable bindings.
class Resolver(private val interpreter: Interpreter) {

  // stack
  // https://stackoverflow.com/questions/12524826/why-should-i-use-deque-over-stack
  // https://stackoverflow.com/questions/46900048/how-can-i-use-stack-in-kotlin
  private val scopes: ArrayDeque<MutableMap<String, Boolean>> = ArrayDeque()

  // push (addLast)
  private fun beginScope(): Unit =
    scopes.addLast(mutableMapOf())
  // pop (removeLast)
  private fun endScope(): Map<String, Boolean> =
    scopes.removeLast()

  // adds the variable to the innermost scope so that it shadows any outer one and so that we know the variable exists
  private fun declare(name: Token) {
    if (!scopes.isEmpty()) {
      // peek (last): looks at the object at the top of this stack without removing it from the stack
      // marks it as "not ready yet" by binding its name to false in the scope map
      scopes.last()[name.lexeme] = false
    }
  }

  // peek (last)
  private fun define(name: Token) {
    if (!scopes.isEmpty()) {
      scopes.last()[name.lexeme] = true
    }
  }

  // single walk over the tree to resolve all the variables it contains
  fun resolve(statements: List<Stmt>): Unit =
    statements.forEach(::resolveStmt)

  // ??? Any
  private fun resolveStmt(statement: Stmt): Any =
    when (statement) {
      is Stmt.Block -> {
        beginScope()
        resolve(statement.statements)
        endScope()
      }
      is Stmt.Expression -> resolveExpr(statement.expression)
      is Stmt.Function -> {
        declare(statement.name)
        define(statement.name)
        resolveFunction(statement)
      }
      is Stmt.If -> {
        resolveExpr(statement.condition)
        resolveStmt(statement.thenBranch)
        resolveStmt(statement.elseBranch)
      }
      is Stmt.Print -> resolveExpr(statement.expression)
      is Stmt.Return -> resolveExpr(statement.value)
      is Stmt.Var -> {
        // splitting binding into two steps: declare and define
        declare(statement.name)
        resolveExpr(statement.initializer)
        define(statement.name)
      }
      is Stmt.While -> {
        resolveExpr(statement.condition)
        resolveStmt(statement.body)
      }
      is Stmt.Empty -> {}
    }

  private fun resolveExpr(expression: Expr): Any =
    when (expression) {
      is Expr.Assign -> {
        resolveExpr(expression.value)
        resolveLocal(expression, expression.name)
      }
      is Expr.Binary -> {
        resolveExpr(expression.left)
        resolveExpr(expression.right)
      }
      is Expr.Call -> {
        resolveExpr(expression.callee)

        for (argument in expression.arguments) {
          resolveExpr(argument)
        }
      }
      is Expr.Grouping -> resolveExpr(expression.expression)
      is Expr.Literal -> {}
      is Expr.Logical -> {
        resolveExpr(expression.left)
        resolveExpr(expression.right)
      }
      is Expr.Unary -> resolveExpr(expression.right)
      is Expr.Variable -> {
        if (!scopes.isEmpty() && scopes.last()[expression.name.lexeme] == false) {
          Lox.error(expression.name, "can't read local variable in its own initializer")
        }
        resolveLocal(expression, expression.name)
      }
      is Expr.Empty -> {}
    }

  // ??? how convoluted is this implementation ???
  // `Interpreter` is injected in the `Resolver` which in turns manipulates its internal `locals` state, why???
  private fun resolveLocal(expression: Expr, name: Token) {
    for (i in scopes.indices.reversed()) {
      // If we find the variable, we resolve it, passing in the number of scopes
      // between the current innermost scope and the scope where the variable was found.
      // So, if the variable was found in the current scope, we pass in 0.
      // If it's in the immediately enclosing scope, 1 and so on/
      // If we walk through all the block scopes and never find the variable, we leave it unresolved and assume it's global.
      if (scopes[i].containsKey(name.lexeme)) {
        interpreter.resolve(expression, scopes.size - 1 - i)
        return
      }
    }
  }

  private fun resolveFunction(function: Stmt.Function) {
    beginScope()
    for (param in function.params) {
      declare(param)
      define(param)
    }
    resolve(function.body)
    endScope()
  }
}
