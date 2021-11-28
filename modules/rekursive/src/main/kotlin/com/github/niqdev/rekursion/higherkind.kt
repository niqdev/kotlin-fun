package com.github.niqdev.rekursion

/**
 * Higher-Kinded Types are NOT supported directly by Kotlin.
 *
 * Given List<A>, List is a `type constructor` that takes a `type parameter` A.
 * List is a first-order-kinded type i.e. * -> *
 *
 * Given Functor<F<A>>, Functor is a `type constructor` that takes a `type constructor parameter` F e.g. List.
 * Functor is a higher-kinded or higher-order type i.e. (* -> *) -> *
 */
interface Kind<out F, out A>

// partially applied type constructor
typealias Kind2<F, A, B> = Kind<Kind<F, A>, B>
