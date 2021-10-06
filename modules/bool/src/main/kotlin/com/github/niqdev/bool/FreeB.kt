package com.github.niqdev.bool

sealed interface FreeB<T> {
  data class Pure<T>(val value: T) : FreeB<T>
  data class Grouping<T>(val fb: FreeB<T>) : FreeB<T>
  class True<T> : FreeB<T>
  class False<T> : FreeB<T>
  data class And<T>(val left: FreeB<T>, val right: FreeB<T>) : FreeB<T>
  data class Or<T>(val left: FreeB<T>, val right: FreeB<T>) : FreeB<T>
  data class Not<T>(val right: FreeB<T>) : FreeB<T>
}

// TODO Validated<NonEmptyList<Error>, Boolean>
// free boolean algebra interpreter
fun <T> FreeB<T>.run(f: (T) -> Boolean): Boolean =
  when (this) {
    is FreeB.Pure -> f(value)
    is FreeB.Grouping -> fb.run(f)
    is FreeB.True -> true
    is FreeB.False -> false
    is FreeB.And -> left.run(f) && right.run(f)
    is FreeB.Or -> left.run(f) || right.run(f)
    is FreeB.Not -> !right.run(f)
  }

fun FreeB<Predicate>.run(): Boolean =
  run { evalPredicate()(it) }

fun FreeB<Predicate>.pretty(): String =
  when (this) {
    is FreeB.Pure -> value.pretty()
    is FreeB.Grouping -> "(${fb.pretty()})"
    is FreeB.True -> "true"
    is FreeB.False -> "false"
    is FreeB.And -> "(${left.pretty()} AND ${right.pretty()}"
    is FreeB.Or -> "(${left.pretty()} OR ${right.pretty()}"
    is FreeB.Not -> "(NOT ${right.pretty()})"
  }

// TODO type class / higher-kinded - Predicate<T> and F<Predicate> ?
sealed interface Predicate {
  data class Identity(val id: Value) : Predicate
  data class Greater(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class GreaterEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class Less(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class LessEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class EqualEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class BangEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  // class In(val left: FreeB<Predicate>, vararg items: Value) : Predicate
  // data class Match(val left: FreeB<Predicate>, val regex: String) : Predicate
}

// TODO incomplete
fun evalPredicate(): (Predicate) -> Boolean =
  { predicate ->
    when (predicate) {
      is Predicate.Greater ->
        when {
          predicate.left is FreeB.Pure &&
            predicate.left.value is Predicate.Identity &&
            predicate.left.value.id is Value.Number &&
            predicate.right is FreeB.Pure &&
            predicate.right.value is Predicate.Identity &&
            predicate.right.value.id is Value.Number ->
            predicate.left.value.id.int > predicate.right.value.id.int
          else -> error("invalid predicate: $predicate")
        }
      is Predicate.Less ->
        when {
          predicate.left is FreeB.Pure &&
            predicate.left.value is Predicate.Identity &&
            predicate.left.value.id is Value.Number &&
            predicate.right is FreeB.Pure &&
            predicate.right.value is Predicate.Identity &&
            predicate.right.value.id is Value.Number ->
            predicate.left.value.id.int < predicate.right.value.id.int
          else -> error("invalid predicate: $predicate")
        }
      is Predicate.EqualEqual ->
        when {
          predicate.left is FreeB.Pure &&
            predicate.left.value is Predicate.Identity &&
            predicate.left.value.id is Value.Number &&
            predicate.right is FreeB.Pure &&
            predicate.right.value is Predicate.Identity &&
            predicate.right.value.id is Value.Number ->
            predicate.left.value.id.int == predicate.right.value.id.int
          else -> error("invalid predicate: $predicate")
        }
      else -> error("invalid predicate: $predicate")
    }
  }

fun Predicate.pretty(): String =
  when (this) {
    is Predicate.Identity -> id.pretty()
    is Predicate.Greater -> "(${left.pretty()} > ${right.pretty()})"
    is Predicate.GreaterEqual -> "(${left.pretty()} >= ${right.pretty()})"
    is Predicate.Less -> "(${left.pretty()} < ${right.pretty()})"
    is Predicate.LessEqual -> "(${left.pretty()} <= ${right.pretty()})"
    is Predicate.EqualEqual -> "(${left.pretty()} == ${right.pretty()})"
    is Predicate.BangEqual -> "(${left.pretty()} != ${right.pretty()})"
  }

sealed interface Value {
  data class Number(val int: Int) : Value
  data class String(val string: kotlin.String) : Value
}

fun Value.pretty(): String =
  when (this) {
    is Value.Number -> "Number($int)"
    is Value.String -> "String($string)"
  }

fun main() {
  val predicates: FreeB<Predicate> =
    FreeB.And(
      left = FreeB.Pure(
        Predicate.Less(
          FreeB.Pure(Predicate.Identity(Value.Number(8))),
          FreeB.Pure(Predicate.Identity(Value.Number(42)))
        )
      ),
      right = FreeB.Pure(
        Predicate.Greater(
          FreeB.Pure(Predicate.Identity(Value.Number(6))),
          FreeB.Pure(Predicate.Identity(Value.Number(3)))
        )
      )
    )

  println(predicates.pretty())
  println(predicates.run())
}
