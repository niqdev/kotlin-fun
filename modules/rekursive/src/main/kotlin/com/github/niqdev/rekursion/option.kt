package com.github.niqdev.rekursion

sealed interface MyOption<out A> {

  object None : MyOption<Nothing> {
    override fun toString(): String = this.show()
  }

  data class Some<out A>(val value: A) : MyOption<A> {
    override fun toString(): String = this.show()
  }

  companion object {
    operator fun <A> invoke(a: A? = null): MyOption<A> =
      when (a) {
        null -> None
        else -> Some(a)
      }
  }
}

fun <A> MyOption<A>.show(): String =
  when (this) {
    is MyOption.None -> "None"
    is MyOption.Some -> "Some($value)"
  }

fun <A> MyOption<A>.isEmpty(): Boolean =
  when (this) {
    is MyOption.None -> true
    is MyOption.Some -> false
  }
