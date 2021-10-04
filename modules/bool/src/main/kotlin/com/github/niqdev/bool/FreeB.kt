package com.github.niqdev.bool

sealed interface FreeB<T> {
  data class Pure<T>(val value: T) : FreeB<T>
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
    is FreeB.Pure -> f(this.value)
    is FreeB.True -> true
    is FreeB.False -> false
    is FreeB.And -> this.left.run(f) && this.right.run(f)
    is FreeB.Or -> this.left.run(f) || this.right.run(f)
    is FreeB.Not -> !this.right.run(f)
  }

fun <T> FreeB<T>.pretty(): String =
  when (this) {
    is FreeB.Pure -> "$value"
    is FreeB.True -> "true"
    is FreeB.False -> "false"
    is FreeB.And -> "(${left.pretty()} AND ${right.pretty()}"
    is FreeB.Or -> "(${left.pretty()} OR ${right.pretty()}"
    is FreeB.Not -> "(NOT ${right.pretty()}"
  }

// TODO type class / higher-kinded - Predicate<T> and F<Predicate> ?
sealed interface Predicate {
  data class Identity(val id: MyValue) : Predicate
  data class Greater(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class GreaterEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class Less(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class LessEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class EqualEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class BangEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class Minus(val item: FreeB<Predicate>) : Predicate
  class In(val left: FreeB<Predicate>, vararg items: MyValue) : Predicate
  data class Match(val left: FreeB<Predicate>, val regex: String) : Predicate

  companion object {
    fun eval(): (Predicate) -> Boolean =
      { predicate ->
        when (predicate) {
          is Greater ->
            when {
              predicate.left is FreeB.Pure &&
                predicate.left.value is Identity &&
                predicate.left.value.id is MyValue.NumberValue &&
                predicate.right is FreeB.Pure &&
                predicate.right.value is Identity &&
                predicate.right.value.id is MyValue.NumberValue ->
                predicate.left.value.id.int > predicate.right.value.id.int
              else -> error("invalid predicate: $predicate")
            }
          is Less ->
            when {
              predicate.left is FreeB.Pure &&
                predicate.left.value is Identity &&
                predicate.left.value.id is MyValue.NumberValue &&
                predicate.right is FreeB.Pure &&
                predicate.right.value is Identity &&
                predicate.right.value.id is MyValue.NumberValue ->
                predicate.left.value.id.int < predicate.right.value.id.int
              else -> error("invalid predicate: $predicate")
            }
          else -> error("invalid predicate: $predicate")
        }
      }
  }
}

sealed interface MyValue {
  data class NumberValue(val int: Int) : MyValue
  data class StringValue(val string: String) : MyValue
  data class KeyValue(val key: String) : MyValue
}

fun main() {
  val predicates: FreeB<Predicate> =
    FreeB.And(
      left = FreeB.Pure(
        Predicate.Less(
          FreeB.Pure(Predicate.Identity(MyValue.NumberValue(8))),
          FreeB.Pure(Predicate.Identity(MyValue.NumberValue(42)))
        )
      ),
      right = FreeB.Pure(
        Predicate.Greater(
          FreeB.Pure(Predicate.Identity(MyValue.NumberValue(6))),
          FreeB.Pure(Predicate.Identity(MyValue.NumberValue(3)))
        )
      )
    )

  println(predicates.run(Predicate.eval()))
}
