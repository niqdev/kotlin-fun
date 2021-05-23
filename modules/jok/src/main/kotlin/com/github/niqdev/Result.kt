package com.github.niqdev

import java.io.Serializable
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.lang.RuntimeException

sealed class Result<out A> : Serializable {

  internal class Failure<out A>(internal val exception: RuntimeException) : Result<A>() {
    override fun toString(): String = "Failure(${exception.message})"
  }

  internal class Success<out A>(internal val value: A) : Result<A>() {
    override fun toString(): String = "Success($value)"
  }

  internal object Empty : Result<Nothing>() {
    override fun toString(): String = "Empty"
  }

  companion object {

    operator fun <A> invoke(a: A? = null): Result<A> =
      when (a) {
        null -> Failure(NullPointerException())
        else -> Success(a)
      }

    // wrap in runtime exception
    fun <A> failure(message: String): Result<A> =
      Failure(IllegalStateException(message))

    // if you need to deal with exceptions, apply the safe principle: always catch, never throw
    fun <A> failure(exception: Throwable): Result<A> =
      when (exception) {
        is RuntimeException -> Failure(exception)
        // wrap checked exception in runtime exception
        else -> Failure(IllegalStateException(exception))
      }

    fun <A> of(a: () -> A): Result<A> =
      try { Result(a()) } catch (e: Throwable) { failure(e) }
  }
}

// ---------- 7.4 ----------

fun <A, B> Result<A>.map(): ((A) -> B) -> Result<B> =
  { f ->
    when (this) {
      is Result.Empty -> Result.Empty
      is Result.Failure -> Result.Failure(this.exception)
      is Result.Success -> try {
        Result.Success(f(this.value))
      } catch (e: Throwable) {
        Result.failure(e)
      }
    }
  }

fun <A, B> Result<A>.flatMap(): ((A) -> Result<B>) -> Result<B> =
  { f ->
    when (this) {
      is Result.Empty -> Result.Empty
      is Result.Failure -> Result.Failure(this.exception)
      is Result.Success -> try {
        f(this.value)
      } catch (e: Throwable) {
        Result.failure(e)
      }
    }
  }

fun <A> Result<A>.getOrElse(): (() -> A) -> A =
  { default ->
    when (this) {
      is Result.Success -> this.value
      else -> default()
    }
  }

// ---------- 7.5 ----------

fun <A> Result<A>.filter(): ((A) -> Boolean) -> Result<A> =
  { p -> this.flatMap<A, A>()() { a -> if (p(a)) Result.Success(a) else Result.failure("invalid") } }

// ---------- 7.6 ----------

fun <A> Result<A>.exists(): ((A) -> Boolean) -> Boolean =
  { p -> this.map<A, Boolean>()(p).getOrElse()() { false } }

// ---------- 7.7 ----------

fun <A> Result<A>.mapFailure(): (String) -> Result<A> =
  { error ->
    when (this) {
      is Result.Empty -> Result.Empty
      is Result.Success -> Result.Success(this.value)
      is Result.Failure -> Result.Failure(RuntimeException(error, this.exception))
    }
  }

// ---------- 7.9 ----------

fun <A> Result<A>.unsafeForEach(): ((A) -> Unit) -> Unit =
  { effect ->
    when (this) {
      is Result.Empty -> Result.Empty
      is Result.Failure -> Result.Failure<A>(this.exception)
      is Result.Success -> effect(this.value)
    }
  }

// ---------- 7.10 ----------
// ---------- 7.11 ----------

fun <A> Result<A>.unsafeForEachOrElse(): ((A) -> Unit) -> ((RuntimeException) -> Unit) -> (() -> Unit) -> Unit =
  { onSuccess ->
    { onFailure ->
      { onEmpty ->
        when (this) {
          is Result.Empty -> onEmpty()
          is Result.Failure -> onFailure(this.exception)
          is Result.Success -> onSuccess(this.value)
        }
      }
    }
  }

// ---------- 7.12 ----------

fun <A, B> ((A) -> B).liftResult(): (Result<A>) -> Result<B> =
  { result -> result.map<A, B>()() { a -> this(a) } }

// ---------- 7.13 ----------

fun <A, B, C> ((A) -> (B) -> C).lift2Result(): (Result<A>) -> (Result<B>) -> Result<C> =
  { resultA -> { resultB -> resultA.flatMap<A, C>()() { a -> resultB.map<B, C>()() { b -> this(a)(b) } } } }

fun <A, B, C, D> ((A) -> (B) -> (C) -> D).lift3Result(): (Result<A>) -> (Result<B>) -> (Result<C>) -> Result<D> =
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

fun <A, B, C> Result<A>.map2(): (Result<B>) -> ((A) -> (B) -> C) -> Result<C> =
  { resultB -> { f -> f.lift2Result()(this)(resultB) } }

// specify the types because of the limited type inference capacity of Kotlin
// use comprehension pattern to compose: N flatMap + map
fun main() {
  println(Result(42).filter()() { it < 100 })
  println(Result(42).filter()() { it > 100 })
  println(Result(42).exists()() { it < 100 })
  println(Result<Int>().exists()() { it < 100 })
  println(Result(42).unsafeForEach()(::println))
  println(Result.failure<Int>("error").unsafeForEachOrElse()(::println)(::println)() { println("empty") })
}
