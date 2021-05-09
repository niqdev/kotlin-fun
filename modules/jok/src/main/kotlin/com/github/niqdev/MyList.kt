package com.github.niqdev

// single linked list
// `sealed classes` allow defining algebraic data types (ADT): types that have a limited set of subtypes
// covariant in A
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

fun <T> MyList<T>.head(): T? =
  when (this) {
    is MyList.MyNil -> null
    is MyList.MyCons -> head
  }

fun <T> MyList<T>.tail(): MyList<T>? =
  when (this) {
    is MyList.MyNil -> null
    is MyList.MyCons -> tail
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

// ------------------------------

fun <A> MyList<A>.concat(): (MyList<A>) -> MyList<A> =
  { list ->
    tailrec fun loop(tmp: MyList<A>, result: MyList<A>): MyList<A> =
      when (tmp) {
        is MyList.MyNil -> result
        is MyList.MyCons -> loop(tmp.tail, result.cons()(tmp.head))
      }
    loop(this, list)
  }

// ---------- 5.5 ----------

// drop last element
// http://zvon.org/other/haskell/Outputprelude/init_f.html
fun <A> MyList<A>.init(): MyList<A> {
  tailrec fun loop(tmp: MyList<A>, result: MyList<A>): MyList<A> =
    when (tmp) {
      is MyList.MyNil -> result
      is MyList.MyCons -> when (tmp.tail) {
        is MyList.MyNil -> result
        is MyList.MyCons -> loop(tmp.tail, result.cons()(tmp.head))
      }
    }
  return loop(this, MyList())
}

fun <A> MyList<A>.reverse(): MyList<A> {
  tailrec fun loop(tmp: MyList<A>, result: MyList<A>): MyList<A> =
    when (tmp) {
      is MyList.MyNil -> result
      is MyList.MyCons -> loop(tmp.tail, result.cons()(tmp.head))
    }
  return loop(this, MyList())
}

// ---------- 5.6 ----------
// ---------- 5.7 ----------

fun MyList<Int>.sum(): Int {
  tailrec fun loop(tmp: MyList<Int>, result: Int): Int =
    when (tmp) {
      is MyList.MyNil -> result
      is MyList.MyCons -> loop(tmp.tail, result + tmp.head)
    }
  return loop(this, 0)
}

// ---------- 5.9 ----------
// ---------- 5.10 ----------

// @UnsafeVariance
// the `absorbing element` for multiplication is 0, also called the `zero element` or `short circuiting` or `identity`

fun <A, B> MyList<A>.foldLeft(): (B) -> (f: (B, A) -> B) -> B =
  { zero ->
    { f ->
      tailrec fun loop(tmp: MyList<A>, result: B): B =
        when (tmp) {
          is MyList.MyNil -> result
          is MyList.MyCons -> loop(tmp.tail, f(result, tmp.head))
        }
      loop(this, zero)
    } 
  }

// ---------- 5.12 ----------
// ---------- 5.13 ----------

fun <A, B> MyList<A>.foldRight(): (B) -> (f: (A) -> (B) -> B) -> B =
  { zero -> { f -> this.foldLeft<A, B>()(zero)() { a, b -> f(b)(a) } } }

// ---------- 5.8 ----------

fun <A> MyList<A>.length(): Int =
  this.foldRight<A, Int>()(0)() { { acc -> acc + 1 } }

// ---------- 5.11 ----------

fun <A> MyList<A>.reverseWithFoldLeft(): MyList<A> =
  this.foldLeft<A, MyList<A>>()(MyList.MyNil as MyList<A>)() { acc, i -> acc.cons()(i) }

// ---------- 5.14 ----------

fun <A> MyList<A>.concatWithFoldLeft(): (MyList<A>) -> MyList<A> =
  { list -> this.reverse().foldLeft<A, MyList<A>>()(list)() { acc, i -> acc.cons()(i) } }

// ---------- 5.15 ----------

fun <A> MyList<MyList<A>>.flatten(): MyList<A> =
  this.reverse().foldLeft<MyList<A>, MyList<A>>()(MyList())() { acc, i -> i.concatWithFoldLeft()(acc) }

fun main() {
  val list: MyList<Int> = MyList(1, 2, 3)
  println(list)
  println(list.cons()(0))
  println(MyList<Int>().setHead()(0))
  println(list.setHead()(0))
  println(list.drop()(2))
  println(MyList(1, 2, 3, 4, 5).dropWhile()() { it < 3 })
  println(MyList(1, 2, 3).concat()(MyList(4, 5, 6))) // 3, 2, 1, 4, 5, 6, Nil
  println(MyList(1, 2, 3, 4, 5).init())
  println(MyList(1, 2, 3, 4, 5).reverse().tail()?.reverse())
  println(MyList(1, 2, 3).sum())
  println(MyList(1, 2, 3, 4, 5).foldLeft<Int, Int>()(0)() { acc, i -> acc + i })
  println(MyList(1, 2, 3, 4, 5).foldLeft<Int, Int>()(1)() { acc, i -> acc * i })
  println(MyList(1, 2, 3).foldLeft<Int, MyList<Int>>()(MyList(4, 5))() { acc, i -> acc.cons()(i) })
  println(MyList(1, 2, 3, 4, 5).length())
  println(MyList(1, 2, 3, 4, 5).reverseWithFoldLeft())
  println(MyList(1, 2, 3).concatWithFoldLeft()(MyList(4, 5)))
  println(MyList(MyList(1, 2), MyList(3), MyList(), MyList(4, 5)).flatten())
}
