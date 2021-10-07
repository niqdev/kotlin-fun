package com.github.niqdev.bool.internal

// TODO not used
sealed class InternalList<out A> {

  abstract fun isEmpty(): Boolean

  internal object Nil : InternalList<Nothing>() {
    override fun isEmpty(): Boolean = true
    override fun toString(): String = "Nil"
  }

  internal class Cons<A>(val head: A, val tail: InternalList<A>) : InternalList<A>() {
    override fun isEmpty(): Boolean = false
    override fun toString(): String {
      tailrec fun loop(tmp: InternalList<A>, result: String): String =
        when (tmp) {
          is Nil -> "${result}Nil)"
          is Cons -> loop(tmp.tail, "$result${tmp.head}, ")
        }
      return loop(this, "List(")
    }
  }

  companion object {

    operator fun <A> invoke(items: List<A>): InternalList<A> {
      // non-stack safe
      fun loop(result: List<A>): InternalList<A> =
        when {
          result.isEmpty() -> Nil
          else -> Cons(result[0], loop(result.drop(1)))
        }
      return loop(items)
    }

    operator fun <A> invoke(vararg items: A): InternalList<A> =
      invoke(items.asList())
  }
}

fun <A> InternalList<A>.cons(head: A): InternalList<A> =
  InternalList.Cons(head, this)

operator fun <A> InternalList<A>.plus(a: A): InternalList<A> =
  this.cons(a)

fun <A, B> InternalList<A>.foldLeft(): (B) -> (f: (B, A) -> B) -> B =
  { zero ->
    { f ->
      tailrec fun loop(tmp: InternalList<A>, result: B): B =
        when (tmp) {
          is InternalList.Nil -> result
          is InternalList.Cons -> loop(tmp.tail, f(result, tmp.head))
        }
      loop(this, zero)
    }
  }

fun <A> InternalList<A>.toList(): List<A> =
  this.foldLeft<A, List<A>>()(listOf())() { acc, i -> acc + i }

fun <A> InternalList<A>.reverse(): InternalList<A> =
  this.foldLeft<A, InternalList<A>>()(InternalList.Nil as InternalList<A>)() { acc, i -> acc.cons(i) }

fun <A, B> InternalList<A>.map(f: (A) -> B): InternalList<B> =
  this.foldLeft<A, InternalList<B>>()(InternalList())() { acc, i -> acc.cons(f(i)) }.reverse()
