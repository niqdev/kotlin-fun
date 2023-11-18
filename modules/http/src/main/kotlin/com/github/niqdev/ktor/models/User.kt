package com.github.niqdev.ktor.models

// https://kotlinlang.org/docs/inline-classes.html
@JvmInline
value class UserId(private val uuid: java.util.UUID) {

  companion object {
    fun unsafeFrom(value: String): Result<UserId> =
      runCatching { java.util.UUID.fromString(value) }.map(::UserId)
  }

  val value: String
    get() = uuid.toString()
}

data class User(val id: UserId, val name: String, val age: Int)
