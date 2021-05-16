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

  companion object {

    operator fun <A> invoke(a: A? = null): Result<A> =
      when (a) {
        null -> Failure(NullPointerException())
        else -> Success(a)
      }

    // wrap in runtime exception
    fun <A> failure(message: String): Result<A> =
      Failure(IllegalStateException(message))

    fun <A> failure(exception: RuntimeException): Result<A> =
      Failure(exception)

    // wrap checked exception in runtime exception
    fun <A> failure(exception: Exception): Result<A> =
      Failure(IllegalStateException(exception))
  }
}

// ---------- 7.4 ----------

// pag 177
