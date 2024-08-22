package com.github.niqdev.json.core

sealed class Either<out E, out T> {
  data class Left<E>(
    val error: E,
  ) : Either<E, Nothing>() {
    override fun toString(): String = show()
  }

  data class Right<T>(
    val value: T,
  ) : Either<Nothing, T>() {
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
    // NEVER catch Throwable, why catching unrecoverable exceptions/errors if the VM is blowing up?
    // https://www.sumologic.com/blog/why-you-should-never-catch-throwable-in-scala
    // https://github.com/arrow-kt/arrow/blob/1.0.0/arrow-libs/core/arrow-core/src/commonMain/kotlin/arrow/core/Either.kt#L1025
    // https://github.com/arrow-kt/arrow/blob/main/arrow-libs/core/arrow-core/src/commonMain/kotlin/arrow/core/nonFatalOrThrow.kt
    // https://github.com/arrow-kt/arrow/blob/main/arrow-libs/core/arrow-core/src/jvmMain/kotlin/arrow/core/NonFatal.kt
    // https://github.com/typelevel/cats/blob/v2.8.0/core/src/main/scala/cats/syntax/either.scala#L393
    // https://github.com/scala/scala/blob/2.13.x/src/library/scala/util/control/NonFatal.scala#L41
    fun <T> catch(block: () -> T): Either<Throwable, T> =
      try {
        Right(block())
      } catch (e: Exception) {
        Left(e)
      }
  }
}

fun <E> E.left(): Either<E, Nothing> = Either.Left(this)

fun <T> T.right(): Either<Nothing, T> = Either.Right(this)
