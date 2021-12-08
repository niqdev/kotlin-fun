package com.github.niqdev.rekursive

/**
 * ForListF is a `surrogate type`: similar to the definition of Nothing.
 * It defines a type, but it prevents the creation of instances.
 */
// Arrow requires you to nest a `companion object` which is used as anchor for the code generation
sealed class ForListF private constructor()

/**
 * ListFOf and ListFPartialOf are just syntactic sugar to reduce the boilerplate.
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

/**
 * Functor Pattern: project a data structure
 *
 * In List<A>, A is the type of the values contained by the list.
 * In ListF<F, A>, A is the type of the value used to represent the tail of a list.
 */
sealed interface ListF<out F, out A> : ListFOf<F, A> {
  data class Cons<out F, out A>(val head: F, val tail: A) : ListF<F, A>
  object Nil : ListF<Nothing, Nothing>
}

// ------------------------------

sealed class ForFix private constructor()

typealias FixOf<F> = Kind<ForFix, F>

@Suppress("NOTHING_TO_INLINE")
inline fun <A> FixOf<A>.fix(): FixOf<A> =
  this as FixOf<A>

/**
 * Fixed-point combinator represent any recursive data type.
 *
 * - the fixed-point of function `f` is `x` such that `f(x) = x`
 * - `fix` is the function that, given a function, returns its fixed-point
 * - if `fix(f)` is the fixed-point of `f`, then `fix(f) = f(fix(f))`
 */
// equivalent in scala: `case class Fix[F[_]](value: F[Fix[F]])
data class Fix<A>(val unfix: Kind<A, FixOf<A>>) : FixOf<A>

// TODO check types: try to generate it with arrow and compare impl
// ??? data class Fix2<F, A>(val unfix: Kind<F, Kind<Fix2<F, A>, F>>): FixOf<A>
