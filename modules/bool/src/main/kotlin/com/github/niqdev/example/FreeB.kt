package com.github.niqdev.example

// https://arosien.github.io/talks/free-boolean-algebras.html
// https://youtu.be/6-afaw_ht80
sealed interface FreeB<A> {

  fun run(f: (A) -> Boolean): Boolean =
    when (this) {
      is Pure -> f(value)
      is True -> true
      is False -> false
      is And -> left.run(f) && right.run(f)
      is Or -> left.run(f) || right.run(f)
      is Not -> !fa.run(f)
    }

  data class Pure<A>(val value: A) : FreeB<A>
  // see data object in kotlin 1.9
  // https://youtu.be/zvwTwGAa41Y
  object True : FreeB<Nothing> {
    override fun toString(): String =
      this::class.simpleName ?: "true"
  }
  object False : FreeB<Nothing>
  data class And<A>(val left: FreeB<A>, val right: FreeB<A>) : FreeB<A>
  data class Or<A>(val left: FreeB<A>, val right: FreeB<A>) : FreeB<A>
  data class Not<A>(val fa: FreeB<A>) : FreeB<A>
}

fun <A> FreeB<A>.pretty(f: (A) -> String): String =
  when (this) {
    is FreeB.Pure -> f(value)
    is FreeB.True -> "true"
    is FreeB.False -> "false"
    is FreeB.And -> "(${left.pretty(f)} && ${right.pretty(f)})"
    is FreeB.Or -> "(${left.pretty(f)} || ${right.pretty(f)})"
    is FreeB.Not -> "!${fa.pretty(f)}"
  }

sealed interface Predicate {
  data class AtLeast13(val i: Int) : Predicate
  data class NonEmptyName(val s: String) : Predicate

  companion object {
    val eval: (Predicate) -> Boolean = {
      when (it) {
        is AtLeast13 -> it.i >= 13
        is NonEmptyName -> it.s.isNotEmpty()
      }
    }
  }
}

fun FreeB<Predicate>.pretty(): String =
  this.pretty {
    when (it) {
      is Predicate.AtLeast13 -> "at-least-13=${it.i}"
      is Predicate.NonEmptyName -> "non-empty=${it.s}"
    }
  }
