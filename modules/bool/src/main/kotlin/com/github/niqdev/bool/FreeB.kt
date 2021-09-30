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

// TODO minus ?
// > >= < <= == != IN MATCH
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

// TODO Predicate<Version>
sealed class Version {
  data class DpkgVersion(val value: String) : Version()
  data class ApkVersion(val value: String) : Version()
  data class RpmVersion(val value: String) : Version()
}

fun <V : Version> Version.compare(other: V): Int =
  when (this) {
    is Version.DpkgVersion -> TODO()
    is Version.ApkVersion -> TODO()
    is Version.RpmVersion -> TODO()
  }

fun main() {
  val predicates: FreeB<Predicate> =
    FreeB.And(
      left = FreeB.Pure(Predicate.Less(8, 42)),
      right = FreeB.Pure(Predicate.Greater(6, 3))
    )

  println(predicates.run(Predicate.eval))
}
