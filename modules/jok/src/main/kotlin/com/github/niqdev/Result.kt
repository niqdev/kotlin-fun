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

fun main() {
  println(Result(42).filter()() { it < 100 })
  println(Result(42).filter()() { it > 100 })
  println(Result(42).exists()() { it < 100 })
  println(Result<Int>().exists()() { it < 100 })
}
