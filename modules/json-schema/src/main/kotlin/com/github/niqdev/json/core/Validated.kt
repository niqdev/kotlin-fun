package com.github.niqdev.json.core

typealias ValidatedNel<E, T> = Validated<NonEmptyList<E>, T>

sealed interface Validated<out E, out T> {
  data class Invalid<E>(
    val error: E,
  ) : Validated<E, Nothing> {
    override fun toString(): String = show()
  }

  data class Valid<T>(
    val value: T,
  ) : Validated<Nothing, T> {
    override fun toString(): String = show()
  }

  fun show(): String =
    when (this) {
      is Invalid -> "Invalid($error)"
      is Valid -> "Valid($value)"
    }

  fun isValid(): Boolean =
    when (this) {
      is Invalid -> false
      is Valid -> true
    }

  fun getInvalidOrNull(): E? =
    when (this) {
      is Invalid -> error
      is Valid -> null
    }
}

fun <E> E.invalid(): Validated<E, Nothing> = Validated.Invalid(this)

fun <T> T.valid(): Validated<Nothing, T> = Validated.Valid(this)
