package com.github.niqdev.rekursive

/*
 * Type classes and ad hoc polymorphism
 *
 * Object-oriented languages use the type system to represent inheritance hierarchies by subtyping classes or interfaces.
 * Subtyping also goes by the name polymorphism: the word polymorphic implies that something may take on multiple forms.
 *
 * Functional programming also has a concept called polymorphism:
 * instead of classic polymorphism, it uses ad hoc polymorphism, and it's achieved by using type classes.
 *
 * "ad hoc" here means polymorphism that is not a fundamental feature of the type system:
 * ad hoc polymorphism is a polymorphism that does not rely on the class hierarchy to bring about flexible design,
 * but instead uses polymorphic functions applied to arguments of different types.
 *
 * A type class is a type system construct that can be applied to implement ad hoc polymorphism,
 * ad hoc polymorphism is achieved by adding constraints to type variables in parametrically polymorphic types.
 * Such a constraint typically involves a type class `T` and a type variable `a`, which means `a` can only be instantiated to a type
 * whose members support the overloaded operations associated with `T`.
 */

// in scala: def map[A, B](fa: F[A])(f: A => B): F[B]

interface Functor<F> {
  fun <A, B> map(fa: Kind<F, A>): ((A) -> B) -> Kind<F, B>
}

fun <F, A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = TODO()
