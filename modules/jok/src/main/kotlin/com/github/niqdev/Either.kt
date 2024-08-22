package com.github.niqdev

// SUM type, covariant in A and B
sealed class Either<out A, out B> {
  internal class Left<out A, out B>(
    internal val value: A,
  ) : Either<A, B>() {
    override fun toString(): String = "Left($value)"
  }

  internal class Right<out A, out B>(
    internal val value: B,
  ) : Either<A, B>() {
    override fun toString(): String = "Right($value)"
  }

  companion object {
    // represent error
    fun <A, B> left(value: A): Either<A, B> = Left(value)

    // represent success
    fun <A, B> right(value: B): Either<A, B> = Right(value)
  }
}

// ---------- 7.1 ----------

fun <A, B, C> Either<A, B>.map(): ((B) -> C) -> Either<A, C> =
  { f ->
    when (this) {
      is Either.Left -> Either.left(this.value)
      is Either.Right -> Either.right(f(this.value))
    }
  }

fun <A, B, C> Either<A, B>.mapLeft(): ((A) -> C) -> Either<C, B> =
  { f ->
    when (this) {
      is Either.Left -> Either.left(f(this.value))
      is Either.Right -> Either.right(this.value)
    }
  }

// ---------- 7.2 ----------

fun <A, B, C> Either<A, B>.flatMap(): ((B) -> Either<A, C>) -> Either<A, C> =
  { f ->
    when (this) {
      is Either.Left -> Either.left(this.value)
      is Either.Right -> f(this.value)
    }
  }

// ---------- 7.3 ----------

fun <A, B> Either<A, B>.getOrElse(): (() -> B) -> B =
  { default ->
    when (this) {
      is Either.Left -> default()
      is Either.Right -> this.value
    }
  }

fun <A, B> Either<A, B>.orElse(): (() -> Either<A, B>) -> Either<A, B> = { default -> default().map<A, B, B> { this.getOrElse { it } } }

fun main() {
  println(Either.right<String, String>("hello").map<String, String, String>()(String::uppercase))
  println(Either.left<String, Int>("error").mapLeft<String, Int, String>()(String::uppercase))
  println(Either.left<String, Int>("error").getOrElse { 42 })
  println(Either.left<String, Int>("error").orElse { Either.left("new-error") })
}
