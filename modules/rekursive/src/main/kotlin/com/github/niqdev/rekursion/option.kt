package com.github.niqdev.rekursion

sealed class ForMyOption private constructor()

typealias MyOptionOf<A> = Kind<ForMyOption, A>

@Suppress("NOTHING_TO_INLINE")
inline fun <A> MyOptionOf<A>.fix(): MyOption<A> =
  this as MyOption<A>

object MyOptionFunctor : Functor<ForMyOption> {
  override fun <A, B> map(fa: MyOptionOf<A>): ((A) -> B) -> MyOptionOf<B> =
    { f ->
      when (val a = fa.fix()) {
        is MyOption.None -> MyOption.None
        is MyOption.Some -> MyOption(f(a.value))
      }
    }
}

sealed interface MyOption<out A> : MyOptionOf<A> {

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
