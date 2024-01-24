package com.github.niqdev.files

import java.io.FileNotFoundException

sealed interface FileFailure {
  data class FileNotFound(val exception: Throwable): FileFailure
  data class Unknown(val exception: Throwable): FileFailure

  companion object {
    fun from(exception: Throwable): FileFailure =
      when (exception) {
        is FileNotFoundException -> FileNotFound(exception)
        else -> Unknown(exception)
      }
  }
}

sealed interface FileResult<T> {
  data class Success<T>(val value: T): FileResult<T>
  data class Failure<T>(val error: FileFailure): FileResult<T>

  companion object {
    // TODO catchNonFatal: Exception vs Throwable
    operator fun <T> invoke(value: T): FileResult<T> =
      try { success(value) } catch (e: Throwable) { failure(e) }

    fun <T> success(value: T): FileResult<T> =
        Success(value)

    fun <T> failure(exception: Throwable): FileResult<T> =
      Failure(FileFailure.from(exception))
  }
}

inline fun <reified T, reified R> FileResult<T>.map(
  f: (T) -> R
): FileResult<R> =
  when (this) {
    is FileResult.Success -> FileResult.Success(f(this.value))
    is FileResult.Failure -> FileResult.Failure(this.error)
  }
