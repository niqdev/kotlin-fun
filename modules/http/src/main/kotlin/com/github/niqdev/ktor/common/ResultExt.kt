package com.github.niqdev.ktor.common

inline fun <A, B> Result<A>.flatMap(onSuccess: (A) -> Result<B>): Result<B> =
  fold(onSuccess, Result.Companion::failure)

fun <A> Result<Result<A>>.flatten(): Result<A> =
  flatMap { it }

inline fun <A, B> Result<A>.transform(
  onSuccess: (A) -> Result<B>,
  onFailure: (Throwable) -> Result<B>
): Result<B> =
  fold(onSuccess) { runCatching { onFailure(it) }.flatten() }

inline fun <A> Result<A>.mapError(onFailure: (Throwable) -> Result<A>): Result<A> =
  fold(Result.Companion::success, onFailure)

inline fun <A> Result<A>.filter(predicate: (A) -> Boolean): Result<A> =
  flatMap { if (predicate(it)) this else Result.failure(java.util.NoSuchElementException("Invalid predicate")) }
