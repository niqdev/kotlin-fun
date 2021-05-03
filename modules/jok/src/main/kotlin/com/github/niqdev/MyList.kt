package com.github.niqdev

// single linked list
// `sealed classes` allow defining algebraic data types (ADT): types that have a limited set of subtypes
sealed class MyList<out A> {

  abstract fun isEmpty(): Boolean

  // empty list
  internal object MyNil : MyList<Nothing>() {
    override fun isEmpty(): Boolean = true
    override fun toString(): String = "Nil"
  }

  // non-empty list: "Cons" means "construct"
  // parameters are declared `internal` so that they won't be visible from outside the file or the module in which the List class is declared
  internal class MyCons<A>(val head: A, val tail: MyList<A>) : MyList<A>() {
    override fun isEmpty(): Boolean = false
    override fun toString(): String {
      tailrec fun loop(tmp: MyList<A>, result: String): String =
        when (tmp) {
          is MyNil -> "${result}Nil"
          is MyCons -> loop(tmp.tail, "$result${tmp.head}, ")
        }
      return loop(this, "")
    }
  }

  companion object {

    // subclasses have been made private, so you must construct lists through calls to the companion object `invoke` function
    // the `invoke` function, together with the modifier `operator`, allows calling the function with a simplified syntax e.g. MyList(1, 2, 3)
    operator fun <A> invoke(vararg items: A): MyList<A> {
      // non-stack safe
      // it doesn't compile with "vararg", cast to List<A>
      fun loop(result: List<A>): MyList<A> =
        when {
          result.isEmpty() -> MyNil
          else -> MyCons(result[0], loop(result.drop(1)))
        }
      return loop(items.asList())
    }
  }
}

// ---------- 5.1 ----------

// add an element at the beginning of a list
fun <A> MyList<A>.cons(): (A) -> MyList<A> =
  { head -> MyList.MyCons(head, this) }

// ---------- 5.2 ----------

fun <A> MyList<A>.setHead(): (A) -> MyList<A>? =
  { head ->
    when (this) {
      is MyList.MyNil -> null
      is MyList.MyCons -> tail.cons()(head)
    }
  }

// ---------- 5.3 ----------

fun <A> MyList<A>.drop(): (Int) -> MyList<A> =
  { n ->
    tailrec fun loop(count: Int, result: MyList<A>): MyList<A> =
      when {
        count >= n -> result
        else -> when (result) {
          is MyList.MyNil -> result
          is MyList.MyCons -> loop(count + 1, result.tail)
        }
      }
    loop(0, this)
  }

// ---------- 5.4 ----------

fun <A> MyList<A>.dropWhile(): ((A) -> Boolean) -> MyList<A> =
  { predicate ->
    tailrec fun loop(result: MyList<A>): MyList<A> =
      when (result) {
        is MyList.MyNil -> result
        is MyList.MyCons -> when {
          predicate(result.head) -> loop(result.tail)
          else -> result
        }
      }
    loop(this)
  }

// ---------- 5.5 ----------

fun main() {
  val list: MyList<Int> = MyList(1, 2, 3)
  println(list)
  println(list.cons()(0))
  println(MyList<Int>().setHead()(0))
  println(list.setHead()(0))
  println(list.drop()(2))
  println(MyList(1, 2, 3, 4, 5).dropWhile()() { it < 3 })
}
