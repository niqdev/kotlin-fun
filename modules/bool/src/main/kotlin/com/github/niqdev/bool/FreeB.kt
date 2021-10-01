package com.github.niqdev.bool

// TODO object True/False ???
sealed interface FreeB<T> {
  data class Pure<T>(val value: T) : FreeB<T>
  class True<T> : FreeB<T>
  class False<T> : FreeB<T>
  data class And<T>(val left: FreeB<T>, val right: FreeB<T>) : FreeB<T>
  data class Or<T>(val left: FreeB<T>, val right: FreeB<T>) : FreeB<T>
  data class Not<T>(val fb: FreeB<T>) : FreeB<T>
}

// free boolean algebra interpreter
// TODO Validated<NonEmptyList<Error>, Boolean>
fun <T> FreeB<T>.run(f: (T) -> Boolean): Boolean =
  when (this) {
    is FreeB.Pure -> f(this.value)
    is FreeB.True -> true
    is FreeB.False -> true
    is FreeB.And -> this.left.run(f) && this.right.run(f)
    is FreeB.Or -> this.left.run(f) || this.right.run(f)
    is FreeB.Not -> !this.fb.run(f)
  }

// TODO Expression
// TODO minus ?
// > >= < <= == != IN MATCH
sealed interface Predicate {
  data class NumberValue(val int: Int) : Predicate
  data class StringValue(val string: String) : Predicate
  data class Greater(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class GreaterEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class Less(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class LessEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class EqualEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate
  data class BangEqual(val left: FreeB<Predicate>, val right: FreeB<Predicate>) : Predicate

  companion object {
    fun eval(): (Predicate) -> Boolean =
      { predicate ->
        when (predicate) {
          is Greater ->
            when {
              predicate.left is FreeB.Pure &&
                predicate.left.value is NumberValue &&
                predicate.right is FreeB.Pure &&
                predicate.right.value is NumberValue ->
                predicate.left.value.int > predicate.right.value.int
              else -> error("TODO String and Validated")
            }
          is GreaterEqual -> TODO()
          is Less ->
            when {
              predicate.left is FreeB.Pure &&
                predicate.left.value is NumberValue &&
                predicate.right is FreeB.Pure &&
                predicate.right.value is NumberValue ->
                predicate.left.value.int < predicate.right.value.int
              else -> error("TODO String and Validated")
            }
          is LessEqual -> TODO()
          is EqualEqual -> TODO()
          is BangEqual -> TODO()
          is NumberValue -> TODO()
          is StringValue -> TODO()
        }
      }
  }
}

sealed interface MyValue {
  data class NumberValue(val value: Int) : MyValue
  data class StringValue(val value: String) : MyValue
  // data class KeyValue(val value: String) : MyValue
}

fun main() {
  val predicates: FreeB<Predicate> =
    FreeB.And(
      left = FreeB.Pure(Predicate.Less(FreeB.Pure(Predicate.NumberValue(8)), FreeB.Pure(Predicate.NumberValue(42)))),
      right = FreeB.Pure(Predicate.Greater(FreeB.Pure(Predicate.NumberValue(6)), FreeB.Pure(Predicate.NumberValue(3))))
    )

  println(predicates.run(Predicate.eval()))
}
