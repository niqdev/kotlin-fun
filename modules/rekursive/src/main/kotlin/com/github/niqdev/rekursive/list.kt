package com.github.niqdev.rekursive

sealed interface MyList<out A> {

  object Nil : MyList<Nothing> {
    override fun toString(): String = this.show()
  }

  class Cons<A>(val head: A, val tail: MyList<A>) : MyList<A> {
    override fun toString(): String = this.show()
  }

  companion object {

    operator fun <A> invoke(items: List<A>): MyList<A> {
      // non-stack safe
      fun loop(result: List<A>): MyList<A> =
        when {
          result.isEmpty() -> Nil
          else -> Cons(result[0], loop(result.drop(1)))
        }
      return loop(items)
    }

    operator fun <A> invoke(vararg items: A): MyList<A> =
      invoke(items.asList())
  }
}

fun <A> MyList<A>.show(): String =
  when (this) {
    is MyList.Nil -> "Nil"
    is MyList.Cons -> {
      tailrec fun loop(tmp: MyList<A>, result: String): String =
        when (tmp) {
          is MyList.Nil -> "${result}Nil)"
          is MyList.Cons -> loop(tmp.tail, "$result${tmp.head}, ")
        }
      loop(this, "List(")
    }
  }

fun <A> MyList<A>.isEmpty(): Boolean =
  when (this) {
    is MyList.Nil -> true
    is MyList.Cons -> false
  }

fun <A> MyList<A>.cons(head: A): MyList<A> =
  MyList.Cons(head, this)

operator fun <A> MyList<A>.plus(a: A): MyList<A> =
  this.cons(a)

fun <A, B> MyList<A>.foldLeft(zero: B): ((B, A) -> B) -> B =
  { f ->
    tailrec fun loop(tmp: MyList<A>, result: B): B =
      when (tmp) {
        is MyList.Nil -> result
        is MyList.Cons -> loop(tmp.tail, f(result, tmp.head))
      }
    loop(this, zero)
  }

fun <A, B> MyList<A>.foldRight(zero: B): ((A, B) -> B) -> B =
  { f -> this.foldLeft(zero)() { a, b -> f(b, a) } }

fun <A, B> MyList<A>.unfold(init: B): ((B) -> MyOption<Pair<A, B>>) -> MyList<A> = TODO()

fun <A> MyList<A>.toList(): List<A> =
  this.foldLeft<A, List<A>>(listOf())() { acc, i -> acc + i }

fun <A> MyList<A>.reverse(): MyList<A> =
  this.foldLeft<A, MyList<A>>(MyList.Nil)() { acc, i -> acc.cons(i) }

fun <A, B> MyList<A>.map(f: (A) -> B): MyList<B> =
  this.foldLeft<A, MyList<B>>(MyList())() { acc, i -> acc.cons(f(i)) }.reverse()
