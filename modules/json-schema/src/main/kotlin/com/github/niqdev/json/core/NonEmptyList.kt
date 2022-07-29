package com.github.niqdev.json.core

class NonEmptyList<out A> internal constructor(
  val head: A,
  val tail: List<A>
) {
  override fun toString(): String =
    "NonEmptyList(${listOf(head) + tail})"

  fun toList(): List<A> =
    listOf(head) + tail

  companion object {

    fun <A> of(item: A, vararg items: A): NonEmptyList<A> =
      NonEmptyList(item, listOf(*items))
  }
}

fun <A> List<A>.toNel(): Either<Throwable, NonEmptyList<A>> =
  when {
    isEmpty() -> IllegalArgumentException("empty").left()
    size == 1 -> NonEmptyList.of(first()).right()
    else -> NonEmptyList(first(), drop(1)).right()
  }
