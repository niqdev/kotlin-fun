package com.github.niqdev

// example
// https://github.com/arrow-kt/arrow/blob/0.12.1/arrow-libs/core/arrow-core-data/src/main/kotlin/arrow/typeclasses/Monoid.kt

// type constructor
// interface Foldable<F<A>> {
//  //some abstract methods
// }
// object ListFoldable : Foldable<List<A>> {
//  //some method implementations with parameterized A
// }

interface Kind<out F, out A>

// surrogate type: express Foldable<List<A>> as Foldable<ForList>,
class ForList private constructor() {
  companion object
}
// syntactic sugar to refer to the higher kind
typealias ListOf<A> = Kind<ForList, A>

//  extend List data type from ListOf to allow downcasting from ListOf to List. This is required because we often need to refer to the concrete type when we access methods on that type
sealed class List<out A> : ListOf<A>

interface Foldable<F> {
  fun <A, B> foldRight(fa: Kind<F, A>, z: B, f: (A, B) -> B): B
}
object ListFoldable : Foldable<ForList> {
  // override fun <A, B> foldRight(fa: Kind<ForList, A>, z: B, f: (A, B) -> B): B = TODO()

  override fun <A, B> foldRight(fa: ListOf<A>, z: B, f: (A, B) -> B): B = TODO()
  // fa.fix().foldRight(z, f)
}

// ability to cast from this higher- kinded type back down to a concrete implementation
//  introducing an extension method called fix on ListOf to turn it back into a concrete List
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> ListOf<A>.fix(): List<A> = this as List<A>

// ---

// Object-oriented languages use the type system to represent inheritance hierarchies by subtyping classes or interfaces.
// Subtyping also goes by the name polymorphism
// The word polymorphic implies that something may take on multiple forms
// Functional programming also has a concept called polymorphism
// instead of classic polymorphism, we use ad hoc polymorphism, and we achieve this by using type classes
// polymorphic functions can be applied to arguments of different types;
// “ad hoc” here means polymorphism that is not a fundamental feature of the type system
// ad hoc polymorphism is a polymorphism that does not rely on the class hierarchy to bring about flexible design but instead uses polymorphic functions applied to arguments of different types.
// A type class is a type system construct that can be applied to implement ad hoc polymorphism
// ad hoc polymorphism is achieved by adding constraints to type variables in parametrically polymorphic types
// Such a constraint typically involves a type class T and a type variable a, which means `a` can only be instantiated to a type whose members support the overloaded operations associated with T.

// Option data type that models optional values and a type class OptionMonad that models the monadic behavior of the Option.
// These two are completely decoupled and share no common inheritance hierarchy.

// TODO pag 469
