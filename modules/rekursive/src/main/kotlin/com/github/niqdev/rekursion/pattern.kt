package com.github.niqdev.rekursion

/**
 * ForListF is a `surrogate type`: similar to the definition of Nothing.
 * It defines a type, but it prevents the creation of instances
 */
// Arrow requires you to nest a `companion object` which is used as anchor for the code generation
sealed class ForListF private constructor()

/**
 * ListFOf and ListFPartialOf are just syntactic sugar to reduce the boilerplate
 */
typealias ListFOf<F, A> = Kind2<ForListF, F, A>
typealias ListFPartialOf<F> = Kind<ForListF, F>

interface ListFFunctor<F> : Functor<ListFPartialOf<F>> {
  override fun <A, B> map(fa: Kind<ListFPartialOf<F>, A>): ((A) -> B) -> Kind<ListFPartialOf<F>, B> =
    { f ->
      when (val a = fa.fix()) {
        is ListF.Nil -> ListF.Nil
        is ListF.Cons -> ListF.Cons(a.head, f(a.tail))
      }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <F, A> ListFOf<F, A>.fix(): ListF<F, A> =
  this as ListF<F, A>

sealed interface ListF<out F, out A> : ListFOf<F, A> {
  data class Cons<out F, out A>(val head: F, val tail: A) : ListF<F, A>
  object Nil : ListF<Nothing, Nothing>
}
