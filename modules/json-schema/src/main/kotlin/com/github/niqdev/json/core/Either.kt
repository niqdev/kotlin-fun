package com.github.niqdev.json.core

sealed class Either<out E, out T> {

  data class Left<E>(val error: E) : Either<E, Nothing>() {
    override fun toString(): String = show()
  }

  data class Right<T>(val value: T) : Either<Nothing, T>() {
    override fun toString(): String = show()
  }

  fun show(): String =
    when (this) {
      is Left -> "Left($error)"
      is Right -> "Right($value)"
    }

  fun toValidated(): Validated<E, T> =
    when (this) {
      is Left -> error.invalid()
      is Right -> value.valid()
    }

  fun <S> map(f: (T) -> S): Either<E, S> =
    when (this) {
      is Left -> this
      is Right -> f(value).right()
    }

  fun <S> mapError(f: (E) -> S): Either<S, T> =
    when (this) {
      is Left -> f(error).left()
      is Right -> this
    }

  fun getOrNull(): T? =
    when (this) {
      is Left -> null
      is Right -> value
    }

  companion object {

    fun <T> catch(block: () -> T): Either<Throwable, T> =
      try { Right(block()) } catch (e: Throwable) { Left(e) }
  }
}

fun <E> E.left(): Either<E, Nothing> =
  Either.Left(this)

fun <T> T.right(): Either<Nothing, T> =
  Either.Right(this)
