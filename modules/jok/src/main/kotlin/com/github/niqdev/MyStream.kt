package com.github.niqdev

sealed class MyStream<out A> {

  internal object Empty : MyStream<Nothing>()

  internal class Cons<out A>(
    internal val head: MyLazy<A>,
    internal val tail: MyLazy<MyStream<A>>
  ) : MyStream<A>()

  companion object {

    operator fun <A> invoke(): MyStream<A> = Empty

    fun <A> cons(
      head: MyLazy<A>,
      tail: MyLazy<MyStream<A>>
    ): MyStream<A> =
      Cons(head, tail)

    fun from(i: Int): MyStream<Int> =
      cons(MyLazy { i }, MyLazy { from(i + 1) })

    // ---------- 9.16 ----------

    fun <A> iterate(): (A) -> ((A) -> A) -> MyStream<A> =
      { seed -> { f -> cons(MyLazy { seed }, MyLazy { iterate<A>()(f(seed))(f) }) } }

    fun fromIterate(i: Int): MyStream<Int> =
      iterate<Int>()(i)() { it + 1 }
  }
}

fun <A> MyStream<A>.head(): Result<A> =
  when (this) {
    is MyStream.Empty -> Result()
    is MyStream.Cons -> Result.of(this.head)
  }

fun <A> MyStream<A>.unsafeHead(): A =
  when (this) {
    is MyStream.Empty -> throw IllegalArgumentException("invalid head")
    is MyStream.Cons -> this.head()
  }

fun <A> MyStream<A>.tail(): Result<MyStream<A>> =
  when (this) {
    is MyStream.Empty -> Result()
    is MyStream.Cons -> Result.of(this.tail)
  }

fun <A> MyStream<A>.unsafeTail(): MyStream<A> =
  when (this) {
    is MyStream.Empty -> MyStream.Empty
    is MyStream.Cons -> this.tail.invoke()
  }

fun <A> MyStream<A>.isEmpty(): Boolean =
  when (this) {
    is MyStream.Empty -> true
    is MyStream.Cons -> false
  }

// ---------- 9.11 ----------

fun <A> (() -> A).repeat(): MyStream<A> =
  MyStream.cons(MyLazy(this), MyLazy { this.repeat() })

// ---------- 9.12 ----------

fun <A> MyStream<A>.takeAtMost(): (Int) -> MyStream<A> =
  { length ->
    tailrec fun loop(stream: MyStream<A>, count: Int, result: MyStream<A>): MyStream<A> =
      when {
        this is MyStream.Cons && (count < length) ->
          loop(stream.unsafeTail(), count + 1, MyStream.cons(MyLazy { stream.unsafeHead() }, MyLazy { result }))
        else -> result
      }
    loop(this, 0, MyStream.Empty)
  }

// ---------- 9.13 ----------
// ---------- 9.14 ----------

fun <A> MyStream<A>.dropAtMost(): (Int) -> MyStream<A> =
  { length ->
    tailrec fun loop(stream: MyStream<A>, count: Int): MyStream<A> {
      return when {
        this is MyStream.Cons && (count < length) ->
          loop(stream.unsafeTail(), count + 1)
        else -> stream
      }
    }
    loop(this, 0)
  }

// ---------- 9.15 ----------

fun <A> MyStream<A>.toList(): MyList<A> {
  tailrec fun loop(stream: MyStream<A>, result: MyList<A>): MyList<A> =
    when (this) {
      is MyStream.Empty -> result
      is MyStream.Cons -> {
        val headList = when (val maybeHead = stream.head()) {
          is Result.Success -> MyList(maybeHead.value)
          else -> MyList.MyNil
        }
        val tailStream = when (val maybeTail = stream.tail()) {
          is Result.Success -> maybeTail.value
          else -> MyStream.Empty
        }
        // println("$headList | $tailStream | $result")
        when {
          headList.isEmpty() -> result
          else -> loop(tailStream, headList.concat()(result))
        }
      }
    }
  return loop(this, MyList())
}

// ---------- 9.17 ----------

fun <A> MyStream<A>.takeWhile(): ((A) -> Boolean) -> MyStream<A> =
  { predicate ->
    tailrec fun loop(stream: MyStream<A>, result: MyStream<A>): MyStream<A> =
      when {
        this is MyStream.Cons && (predicate(stream.unsafeHead())) ->
          loop(stream.unsafeTail(), MyStream.cons(MyLazy { stream.unsafeHead() }, MyLazy { result }))
        else -> result
      }
    loop(this, MyStream.Empty)
  }

// ---------- 9.18 ----------

fun <A> MyStream<A>.dropWhile(): ((A) -> Boolean) -> MyStream<A> =
  { predicate ->
    tailrec fun loop(stream: MyStream<A>): MyStream<A> {
      return when {
        this is MyStream.Cons && (predicate(stream.unsafeHead())) ->
          loop(stream.unsafeTail())
        else -> stream
      }
    }
    loop(this)
  }

// ---------- 9.19 ----------

fun <A> MyStream<A>.exists(): ((A) -> Boolean) -> Boolean =
  { p ->
    tailrec fun loop(stream: MyStream<A>): Boolean =
      when (stream) {
        is MyStream.Empty -> false
        is MyStream.Cons ->
          when {
            p(stream.unsafeHead()) -> true
            else -> loop(stream.unsafeTail())
          }
      }
    loop(this)
  }

// ---------- 9.20 ----------

fun <A, B> MyStream<A>.foldRight(): (MyLazy<B>) -> ((A) -> (MyLazy<B>) -> B) -> B =
  { zero ->
    { f ->
      when (this) {
        is MyStream.Empty -> zero()
        is MyStream.Cons -> f(this.unsafeHead())(MyLazy { this.unsafeTail().foldRight<A, B>()(zero)(f) })
      }
    } 
  }

// FIXME ???
fun <A, B> MyStream<A>.foldRight0(): (MyLazy<B>) -> ((A) -> (MyLazy<B>) -> B) -> B =
  { zero ->
    { f ->
      tailrec fun loop(stream: MyStream<A>, result: MyLazy<B>): B =
        when (stream) {
          is MyStream.Empty -> result()
          is MyStream.Cons -> loop(stream.unsafeTail(), MyLazy { f(stream.unsafeHead())(result) })
        }
      loop(this, zero)
    } 
  }

// ---------- 9.21 ----------

fun <A> MyStream<A>.takeWhileWithFoldRight(): ((A) -> Boolean) -> MyStream<A> =
  { p -> this.foldRight<A, MyStream<A>>()(MyLazy { MyStream.Empty })() { a -> { lazyB -> if (p(a)) MyStream.cons(MyLazy { a }, lazyB) else lazyB() } } }

// ---------- 9.22 ----------

fun <A> MyStream<A>.headSafe(): Result<A> =
  this.foldRight<A, Result<A>>()(MyLazy { Result.Empty })() { a -> { Result.of { a } } }

fun main() {
  println(MyStream.from(3).head())

  println(({ 42 }).repeat().takeAtMost()(5).toList())
  println(MyStream.from(3).dropAtMost()(39).takeAtMost()(8).toList())
  println(MyStream.from(0).dropAtMost()(60000).takeAtMost()(60000))
  println(MyStream.fromIterate(-10).takeAtMost()(5).toList())
  println(MyStream.from(0).takeWhile()() { it < 5 }.toList())
  println(MyStream.from(0).dropWhile()() { it < 5 }.takeAtMost()(5).toList())
  println(MyStream.from(0).exists()() { it == 8 })
  println(MyStream.from(1).takeAtMost()(5).foldRight<Int, Int>()(MyLazy { 10 })() { a -> { lazyB -> lazyB() + a } })
  println(MyStream.from(0).takeWhileWithFoldRight()() { it < 5 }.toList())
  println(MyStream.from(3).headSafe())
  println(MyStream.Empty.headSafe())
}
