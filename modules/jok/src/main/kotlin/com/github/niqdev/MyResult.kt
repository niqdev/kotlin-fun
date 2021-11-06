package com.github.niqdev

import java.io.Serializable
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.lang.RuntimeException

sealed interface MyResult<out A> : Serializable {

  class Failure<out A>(internal val exception: RuntimeException) : MyResult<A> {
    override fun toString(): String = "Failure(${exception.message})"
  }

  class Success<out A>(internal val value: A) : MyResult<A> {
    override fun toString(): String = "Success($value)"
  }

  object Empty : MyResult<Nothing> {
    override fun toString(): String = "Empty"
  }

  companion object {

    operator fun <A> invoke(a: A? = null): MyResult<A> =
      when (a) {
        null -> Failure(NullPointerException())
        else -> Success(a)
      }

    // wrap in runtime exception
    fun <A> failure(message: String): MyResult<A> =
      Failure(IllegalStateException(message))

    // if you need to deal with exceptions, apply the safe principle: always catch, never throw
    fun <A> failure(exception: Throwable): MyResult<A> =
      when (exception) {
        is RuntimeException -> Failure(exception)
        // wrap checked exception in runtime exception
        else -> Failure(IllegalStateException(exception))
      }

    fun <A> of(a: () -> A): MyResult<A> =
      try { MyResult(a()) } catch (e: Throwable) { failure(e) }
  }
}

// ---------- 7.4 ----------

fun <A, B> MyResult<A>.map(): ((A) -> B) -> MyResult<B> =
  { f ->
    when (this) {
      is MyResult.Empty -> MyResult.Empty
      is MyResult.Failure -> MyResult.Failure(this.exception)
      is MyResult.Success -> try {
        MyResult.Success(f(this.value))
      } catch (e: Throwable) {
        MyResult.failure(e)
      }
    }
  }

fun <A, B> MyResult<A>.flatMap(): ((A) -> MyResult<B>) -> MyResult<B> =
  { f ->
    when (this) {
      is MyResult.Empty -> MyResult.Empty
      is MyResult.Failure -> MyResult.Failure(this.exception)
      is MyResult.Success -> try {
        f(this.value)
      } catch (e: Throwable) {
        MyResult.failure(e)
      }
    }
  }

fun <A> MyResult<A>.getOrElse(): (() -> A) -> A =
  { default ->
    when (this) {
      is MyResult.Success -> this.value
      else -> default()
    }
  }

fun <A> MyResult<A>.orElse(): (() -> MyResult<A>) -> MyResult<A> =
  { default ->
    when (this) {
      is MyResult.Success -> this
      else -> default()
    }
  }

// ---------- 7.5 ----------

fun <A> MyResult<A>.filter(): ((A) -> Boolean) -> MyResult<A> =
  { p -> this.flatMap<A, A>()() { a -> if (p(a)) MyResult.Success(a) else MyResult.failure("invalid") } }

// ---------- 7.6 ----------

fun <A> MyResult<A>.exists(): ((A) -> Boolean) -> Boolean =
  { p -> this.map<A, Boolean>()(p).getOrElse()() { false } }

// ---------- 7.7 ----------

fun <A> MyResult<A>.mapFailure(): (String) -> MyResult<A> =
  { error ->
    when (this) {
      is MyResult.Empty -> MyResult.Empty
      is MyResult.Success -> MyResult.Success(this.value)
      is MyResult.Failure -> MyResult.Failure(RuntimeException(error, this.exception))
    }
  }

// ---------- 7.9 ----------

fun <A> MyResult<A>.unsafeForEach(): ((A) -> Unit) -> Unit =
  { effect ->
    when (this) {
      is MyResult.Empty -> MyResult.Empty
      is MyResult.Failure -> MyResult.Failure<A>(this.exception)
      is MyResult.Success -> effect(this.value)
    }
  }

// ---------- 7.10 ----------
// ---------- 7.11 ----------

fun <A> MyResult<A>.unsafeForEachOrElse(): ((A) -> Unit) -> ((RuntimeException) -> Unit) -> (() -> Unit) -> Unit =
  { onSuccess ->
    { onFailure ->
      { onEmpty ->
        when (this) {
          is MyResult.Empty -> onEmpty()
          is MyResult.Failure -> onFailure(this.exception)
          is MyResult.Success -> onSuccess(this.value)
        }
      }
    }
  }

// ---------- 7.12 ----------

fun <A, B> ((A) -> B).liftResult(): (MyResult<A>) -> MyResult<B> =
  { result -> result.map<A, B>()() { a -> this(a) } }

// ---------- 7.13 ----------

fun <A, B, C> ((A) -> (B) -> C).lift2Result(): (MyResult<A>) -> (MyResult<B>) -> MyResult<C> =
  { resultA -> { resultB -> resultA.flatMap<A, C>()() { a -> resultB.map<B, C>()() { b -> this(a)(b) } } } }

fun <A, B, C, D> ((A) -> (B) -> (C) -> D).lift3Result(): (MyResult<A>) -> (MyResult<B>) -> (MyResult<C>) -> MyResult<D> =
  { resultA ->
    { resultB ->
      { resultC ->
        resultA.flatMap<A, D>()() { a ->
          resultB.flatMap<B, D>()() { b ->
            resultC.map<C, D>()() { c ->
              this(a)(b)(c)
            }
          }
        }
      }
    }
  }

// ---------- 7.14 ----------

fun <A, B, C> MyResult<A>.map2(): (MyResult<B>) -> ((A) -> (B) -> C) -> MyResult<C> =
  { resultB -> { f -> f.lift2Result()(this)(resultB) } }

// ------------------------------

fun <A> MyResult<A>.toOption(): Option<A> =
  when (this) {
    is MyResult.Empty -> Option.None
    is MyResult.Failure -> Option.None
    is MyResult.Success -> Option(value)
  }

// ------------------------------

// specify the types because of the limited type inference capacity of Kotlin
// use comprehension pattern to compose: N flatMap + map
fun main() {
  println(MyResult(42).filter()() { it < 100 })
  println(MyResult(42).filter()() { it > 100 })
  println(MyResult(42).exists()() { it < 100 })
  println(MyResult<Int>().exists()() { it < 100 })
  println(MyResult(42).unsafeForEach()(::println))
  println(MyResult.failure<Int>("error").unsafeForEachOrElse()(::println)(::println)() { println("empty") })
}
