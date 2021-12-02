package com.github.niqdev.rekursion

// in scala: def map[A, B](fa: F[A])(f: A => B): F[B]

interface Functor<F> {
  fun <A, B> map(fa: Kind<F, A>): ((A) -> B) -> Kind<F, B>
}

fun <F, A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = TODO()
