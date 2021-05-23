package com.github.niqdev

// data structure: single linked list
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
          is MyNil -> "${result}Nil)"
          is MyCons -> loop(tmp.tail, "$result${tmp.head}, ")
        }
      return loop(this, "MyList(")
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

fun <T> MyList<T>.unsafeHead(): T? =
  when (this) {
    is MyList.MyNil -> null
    is MyList.MyCons -> head
  }

// ---------- 8.2 ----------

fun <T> MyList<T>.head(): Result<T> =
  when (this) {
    is MyList.MyNil -> Result.Empty
    is MyList.MyCons -> Result(head)
  }

// ---------- 8.4 ----------

// keep always the first value
fun <T> MyList<T>.headWithFold(): Result<T> =
  this.foldLeft<T, Result<T>>()(Result.Empty)() { result, t -> if (result is Result.Empty) Result(t) else result }

// ---------- 8.3 ----------

// keep always the last value
fun <T> MyList<T>.last(): Result<T> =
  this.foldLeft<T, Result<T>>()(Result.Empty)() { _, t -> Result(t) }

// ------------------------------

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
// `foldLeft` and `foldRight` are only ways to abstract recursion

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

// ---------- 5.16 ----------
// ---------- 5.17 ----------

fun MyList<Int>.timesThree(): MyList<Int> =
  this.reverse().foldLeft<Int, MyList<Int>>()(MyList())() { acc, i -> acc.cons()(i * 3) }

// ---------- 5.18 ----------

fun <A, B> MyList<A>.map(f: (A) -> B): MyList<B> =
  this.foldLeft<A, MyList<B>>()(MyList())() { acc, i -> acc.cons()(f(i)) }.reverse()

// ---------- 5.19 ----------

fun <A> MyList<A>.filter(p: (A) -> Boolean): MyList<A> =
  this.foldLeft<A, MyList<A>>()(MyList())() { acc, i -> if (p(i)) acc.cons()(i) else acc }.reverse()

// ---------- 5.20 ----------

fun <A, B> MyList<A>.flatMap(f: (A) -> MyList<B>): MyList<B> =
  this.map(f).flatten()

// ---------- 5.21 ----------

fun <A> MyList<A>.filterWithFlatMap(p: (A) -> Boolean): MyList<A> =
  this.flatMap { i -> if (p(i)) MyList(i) else MyList.MyNil }

// ---------- 6.11 ----------

fun <A> MyList<Option<A>>.sequence(): Option<MyList<A>> =
  when {
    this.isEmpty() -> Option.None
    else -> this.foldLeft<Option<A>, Option<MyList<A>>>()(Option(MyList()))() { result, item ->
      when (item) {
        is Option.None -> Option.None
        is Option.Some -> result.map { it.cons()(item.value) }
      }
    }.map { it.reverse() }
  }

fun <A> MyList<Option<A>>.sequenceWithTraverse(): Option<MyList<A>> =
  this.traverse<Option<A>, A>()() { it }

// ---------- 6.12 ----------

fun <A, B> MyList<A>.traverse(): ((A) -> Option<B>) -> Option<MyList<B>> =
  { f -> this.map(f).foldRight<Option<B>, Option<MyList<B>>>()(Option(MyList()))() { item -> { result -> result.flatMap { r -> item.map { i -> r.cons()(i) } } } } }

fun <A, B> MyList<A>.traverseWithMap2(): ((A) -> Option<B>) -> Option<MyList<B>> =
  { f -> this.foldRight<A, Option<MyList<B>>>()(Option(MyList()))() { item -> { result -> f(item).map2<B, MyList<B>, MyList<B>>()(result)() { i, r -> r.cons()(i) } } } }

// ------------------------------

fun <A : Comparable<A>> MyList<A>.maxEither(): Either<String, A> =
  when (this) {
    is MyList.MyNil -> Either.left("empty list")
    is MyList.MyCons -> Either.right(
      this.foldLeft<A, A>()(this.head)() { max, i ->
        if (max.compareTo(i) == 0) max else i
      }
    )
  }

// ---------- 8.5 ----------

fun <A> MyList<Result<A>>.flattenSuccessWithFilter(): MyList<A> =
  this.filter { it is Result.Success }.map { it.getOrElse()() { throw IllegalStateException("impossible") } }

fun <A> MyList<Result<A>>.flattenSuccess(): MyList<A> =
  this.flatMap { result -> result.map<A, MyList<A>>()() { MyList(it) }.getOrElse()() { MyList.MyNil } }

// ---------- 8.6 ----------

// what about higher-kinded types constructor ?
fun <A> MyList<Result<A>>.sequence(): Result<MyList<A>> =
  this.foldLeft<Result<A>, Result<MyList<A>>>()(Result(MyList()))() { result, item ->
    result.map2<MyList<A>, A, MyList<A>>()(item)() { list ->
      { a ->
        list.cons()(a)
      }
    }
  }

// ---------- 8.7 ----------

fun <A, B> MyList<A>.traverseResult(): ((A) -> Result<B>) -> Result<MyList<B>> =
  { f ->
    this.foldRight<A, Result<MyList<B>>>()(Result(MyList()))() { a ->
      {
        it.map2<MyList<B>, B, MyList<B>>()(f(a))() {
          it.cons()::invoke
        }
      }
    }
  }

// ---------- 8.8 ----------

// `zipping` is the process of assembling two lists into one by combining the elements having the same index
// `unzipping` is the reverse procedure, consisting of making two lists out of one by deconstructing the elements

fun <A, B, C> MyList<A>.zipWith(): (MyList<B>) -> ((A) -> (B) -> C) -> MyList<C> =
  {
    { f ->
      tailrec fun loop(la: MyList<A>, lb: MyList<B>, result: MyList<C>): MyList<C> =
        when (la) {
          is MyList.MyNil -> result
          is MyList.MyCons -> when (lb) {
            is MyList.MyNil -> result
            is MyList.MyCons -> loop(la.tail, lb.tail, result.cons()(f(la.head)(lb.head)))
          }
        }
      loop(this, it, MyList())
    }
  }

// ---------- 8.9 ----------

fun <A> A.repeat(): (Int) -> MyList<A> =
  { count ->
    tailrec fun loop(tmpCount: Int, result: MyList<A>): MyList<A> =
      when {
        tmpCount <= 0 -> result
        else -> loop(tmpCount - 1, result.cons()(this))
      }
    loop(count, MyList())
  }

fun MyList<String>.productString(): (MyList<String>) -> MyList<String> =
  { list -> this.foldRight<String, MyList<String>>()(MyList())() { item -> { result -> result.concat()(item.repeat()(list.length()).zipWith<String, String, String>()(list)() { s1 -> { s2 -> s1 + s2 } }) } } }

fun <A, B, C> MyList<A>.product(): (MyList<B>) -> ((A, B) -> C) -> MyList<C> =
  { listB -> { f -> this.flatMap { a -> listB.map { b -> f(a, b) } } } }

// ---------- 8.10 ----------

fun <A, B> MyList<Pair<A, B>>.unzipPair(): Pair<MyList<A>, MyList<B>> =
  this.unzip<Pair<A, B>, A, B>()() { it }

// ---------- 8.11 ----------

fun <A, A1, A2> MyList<A>.unzip(): ((A) -> Pair<A1, A2>) -> Pair<MyList<A1>, MyList<A2>> =
  { f ->
    this.map(f).foldRight<Pair<A1, A2>, Pair<MyList<A1>, MyList<A2>>>()(MyList<A1>() to MyList<A2>())() { item ->
      { it.first.cons()(item.first) to it.second.cons()(item.second) }
    }
  }

// ---------- 8.12 ----------
// ---------- 8.13 ----------

fun <A> MyList<A>.getAt(): (Int) -> Result<A> =
  { index ->
    this.foldRight<A, Pair<Int, Result<A>>>()(0 to Result.Empty)() { item ->
      { result ->
        when {
          (result.first + 1) != index -> result.first + 1 to result.second
          else -> index to Result(item)
        }
      }
    }.second
  }

// ---------- 8.14 ----------
// ---------- 8.15 ----------

fun <A> MyList<A>.splitAt(): (Int) -> Pair<MyList<A>, MyList<A>> =
  { index ->
    tailrec fun loop(i: Int, list: MyList<A>, result: Pair<MyList<A>, MyList<A>>): Pair<MyList<A>, MyList<A>> =
      when {
        i <= index -> when (list) {
          is MyList.MyNil -> result.first.reverse() to result.second
          is MyList.MyCons -> loop(i + 1, list.tail, result.first.cons()(list.head) to result.second)
        }
        else -> result.first.reverse() to result.second.concat()(list)
      }
    loop(0, this, MyList<A>() to MyList<A>())
  }

// ---------- 8.16 ----------

fun <A> MyList<A>.contains(): (MyList<A>) -> Boolean =
  { subList ->
    tailrec fun loop(list: MyList<A>, currentSubList: MyList<A>): Boolean =
      when (list) {
        is MyList.MyNil ->
          // a match has been found (ignore empty sublist)
          currentSubList.isEmpty() && !subList.isEmpty()
        is MyList.MyCons ->
          when (currentSubList) {
            is MyList.MyNil ->
              // a match has been found (ignore empty sublist)
              !subList.isEmpty()
            is MyList.MyCons ->
              when {
                // possible match: advance sublist
                list.head == currentSubList.head -> loop(list.tail, currentSubList.tail)
                // continue searching: restart sublist
                else -> loop(list.tail, subList)
              }
          }
      }
    loop(this, subList)
  }

// ---------- 8.17 ----------

fun <A, B> MyList<A>.groupBy(): ((A) -> B) -> Map<B, MyList<A>> =
  { f ->
    tailrec fun loop(list: MyList<A>, result: Map<B, MyList<A>>): Map<B, MyList<A>> =
      when (list) {
        is MyList.MyNil ->
          result
        is MyList.MyCons -> {
          val key = f(list.head)
          val value = result.getOrDefault(key, MyList())
          loop(list.tail, result.plus(key to value.cons()(list.head)))
        }
      }
    loop(this, mapOf())
  }

// ---------- 8.18 ----------

fun <A, B> A.unfold(): ((A) -> (Option<Pair<A, B>>)) -> MyList<B> =
  { f ->
    tailrec fun loop(item: A, result: MyList<B>): MyList<B> =
      when (val maybe = f(item)) {
        is Option.None -> result
        is Option.Some -> loop(maybe.value.first, result.cons()(maybe.value.second))
      }
    loop(this, MyList())
  }

// ---------- 8.19 ----------

fun Int.range(): (Int) -> MyList<Int> =
  { max -> this.unfold<Int, Int>()() { if (it < max) Option(it + 1 to it) else Option.None }.reverse() }

// ---------- 8.20 ----------

fun <A> MyList<A>.exists(): ((A) -> Boolean) -> Boolean =
  { f -> this.foldLeft<A, Boolean>()(false)() { result, item -> if (f(item)) true else result } }

// ---------- 8.21 ----------

fun <A> MyList<A>.forAll(): ((A) -> Boolean) -> Boolean =
  { f -> this.foldLeft<A, Boolean>()(true)() { result, item -> f(item) && result } }

// ---------- 8.22 ----------

fun <A> MyList<A>.splitListAt(): (Int) -> MyList<MyList<A>> =
  { index ->
    if (this.isEmpty()) MyList()
    else {
      val pair = this.splitAt()(this.length() / index)
      MyList(pair.first, pair.second)
    }
  }

fun <A> MyList<A>.divide(): (Int) -> MyList<MyList<A>> =
  { index ->
    this.splitListAt()(index).foldLeft<MyList<A>, MyList<MyList<A>>>()(MyList())() { result, item ->
      if (item.length() > index) result.concat()(item.divideByTwo())
      else result.cons()(item)
    }
  }

fun <A> MyList<A>.divideByTwo(): MyList<MyList<A>> =
  this.divide()(2)

// ---------- 8.23 ----------
// ---------- 8.24 ----------

// see https://github.com/pysaumont/fpinkotlin/blob/master/fpinkotlin-parent/fpinkotlin-advancedlisthandling-solutions/src/main/kotlin/com/fpinkotlin/advancedlisthandling/exercise23/List.kt
fun <A, B> MyList<A>.parFoldLeft(es: java.util.concurrent.ExecutorService): (B) -> ((B, A) -> B) -> ((B, B) -> B) -> Result<B> = TODO()

// ---------- 9.8 ----------

fun <A> MyList<MyLazy<A>>.sequence(): MyLazy<MyList<A>> =
  MyLazy { this.map { it.invoke() } }

// ---------- 9.9 ----------

fun <A> MyList<MyLazy<A>>.sequenceResult(): MyLazy<Result<MyList<A>>> =
  MyLazy { this.map { Result.of(it) }.sequence() }

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
  println(MyList(1, 2, 3, 4, 5).foldRight<Int, Int>()(1)() { i -> { acc -> acc * i } })
  println(MyList(1, 2, 3, 4, 5).length())
  println(MyList(1, 2, 3, 4, 5).reverseWithFoldLeft())
  println(MyList(1, 2, 3).concatWithFoldLeft()(MyList(4, 5)))
  println(MyList(MyList(1, 2), MyList(3), MyList(), MyList(4, 5)).flatten())
  println(MyList(1, 2, 3, 4, 5).timesThree())
  println(MyList(1, 2, 3, 4, 5).map { it * 3 })
  println(MyList(1, 2, 3, 4, 5).filter { it % 2 == 0 }) // even
  println(MyList(1, 2, 3).flatMap { i -> MyList(i, -i) })
  println(MyList(1, 2, 3, 4, 5).filterWithFlatMap { it % 2 != 0 })
  println(MyList(Option.Some(1), Option.Some(2), Option.Some(3)).sequence())
  println(MyList(Option.Some(1), Option.Some(2), Option.Some(3)).sequenceWithTraverse())
  println(MyList(Option.Some(1), Option.None, Option.Some(3)).sequence())
  println(MyList(Option.Some(1), Option.None, Option.Some(3)).sequenceWithTraverse())
  println(MyList<Option<Int>>().sequence())
  println(MyList<Option<Int>>().sequenceWithTraverse())
  println(MyList("1", "2", "3").traverse<String, Int>()(hLift<String, Int>()(String::toInt)))
  println(MyList("1", "2", "3").traverseWithMap2<String, Int>()(hLift<String, Int>()(String::toInt)))
  println(MyList("1", "aaa", "3").traverse<String, Int>()(hLift<String, Int>()(String::toInt)))
  println(MyList(1, 2, 3, 4, 5).maxEither())
  println(MyList<Int>().maxEither())
  println(MyList<Int>().head())
  println(MyList(1, 2, 3, 4, 5).last())
  println(MyList(1, 2, 3, 4, 5).headWithFold())
  println(MyList(Result(1), Result.failure("error"), Result(2)).flattenSuccessWithFilter())
  println(MyList(Result(1), Result.failure("error"), Result(2)).flattenSuccess())
  println(MyList(Result.failure<Int>("error")).flattenSuccess())
  println(MyList(Result(1), Result(2), Result(3)).sequence())
  println(MyList(Result(1), Result.failure("error"), Result(2)).sequence())
  println(MyList(1, 1, 1).zipWith<Int, String, Pair<Int, String>>()(MyList("a", "b", "c", "d"))() { int -> { string -> int to string } })
  println(1.repeat()(5))
  println(MyList("a", "b", "c").productString()(MyList("1", "2", "3")))
  println(MyList(1, 2).product<Int, Int, Pair<Int, Int>>()(MyList(4, 5, 6))() { a, b -> a to b })
  println(MyList(1 to "a", 2 to "b", 3 to "c").unzipPair())
  println(MyList(1, 2, 3, 4, 5).getAt()(8))
  println(MyList(1, 2, 3, 4, 5).getAt()(2))
  println(MyList(1, 2, 3, 4, 5).splitAt()(2))
  println(MyList(1, 2, 3, 4, 5).splitAt()(-1))
  println(MyList(1, 2, 3, 4, 5).contains()(MyList(3, 4, 5)))
  println(MyList(1, 2, 3, 4, 5).contains()(MyList(2, 4, 5)))
  println(MyList("a1", "b1", "a2", "c1", "d1").groupBy<String, String>()() { if (it.startsWith("a")) "A" else "other" })
  println(0.unfold<Int, Int>()() { if (it < 10) Option(it + 1 to it) else Option.None })
  println(3.range()(8))
  println(MyList(1, 2, 3, 4, 5).exists()() { it == 3 })
  println(MyList(1, 2, 3, 4, 5).exists()() { it == 8 })
  println(MyList(2, 4, 6, 8).forAll()() { it % 2 == 0 })
  println(MyList(1, 2, 3, 4, 5).forAll()() { it % 2 == 0 })
  println(MyList(1, 2, 3, 4, 5).splitListAt()(2))
  println(MyList<Int>().splitListAt()(2))
  println(MyList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).divideByTwo())
  println(MyList(1, 2, 3).divideByTwo())
  println(MyList(1, 2).divideByTwo())
  println(MyList(MyLazy { 1 }, MyLazy { 2 }, MyLazy { 3 }).sequence().invoke())
  println(MyList(MyLazy { 1 }, MyLazy { 2 }, MyLazy { throw IllegalArgumentException() }).sequenceResult().invoke())
}
