package com.github.niqdev.bool

// TODO object True/False ???
sealed class FreeB<T> {
  data class Pure<T>(val value: T) : FreeB<T>()
  class True<T> : FreeB<T>()
  class False<T> : FreeB<T>()
  data class And<T>(val left: FreeB<T>, val right: FreeB<T>) : FreeB<T>()
  data class Or<T>(val left: FreeB<T>, val right: FreeB<T>) : FreeB<T>()
  data class Not<T>(val fb: FreeB<T>) : FreeB<T>()
}

fun <T> FreeB<T>.run(f: (T) -> Boolean): Boolean =
  when (this) {
    is FreeB.Pure -> f(this.value)
    is FreeB.True -> true
    is FreeB.False -> true
    is FreeB.And -> this.left.run(f) && this.right.run(f)
    is FreeB.Or -> this.left.run(f) || this.right.run(f)
    is FreeB.Not -> !this.fb.run(f)
  }

sealed class Predicate {
  data class Greater(val left: Int, val right: Int) : Predicate()
  data class Less(val left: Int, val right: Int) : Predicate()

  companion object {
    val eval: (Predicate) -> Boolean =
      { predicate ->
        when (predicate) {
          is Greater -> predicate.left > predicate.right
          is Less -> predicate.left < predicate.right
        }
      }
  }
}

fun main() {
  val predicates: FreeB<Predicate> =
    FreeB.And(
      left = FreeB.Pure(Predicate.Less(8, 42)),
      right = FreeB.Pure(Predicate.Greater(6, 3))
    )

  println(predicates.run(Predicate.eval))
}
