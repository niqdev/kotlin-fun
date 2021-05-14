package com.github.niqdev

sealed class Option<out A> {
  abstract fun isEmpty(): Boolean

  internal object None : Option<Nothing>() {
    override fun isEmpty(): Boolean = true
    override fun toString(): String = "None"
    // override fun equals(other: Any?): Boolean = other === None
    // override fun hashCode(): Int = 0
  }

  internal data class Some<out A>(internal val value: A) : Option<A>() {
    override fun isEmpty(): Boolean = false
    override fun toString(): String = "Some($value)"
  }

  companion object {
    operator fun <A> invoke(a: A? = null): Option<A> =
      when (a) {
        null -> None
        else -> Some(a)
      }
  }
}

fun List<Int>.maybeMax(): Option<Int> = Option(this.max())

// ---------- 6.1 ----------

// @UnsafeVariance is really needed ???
fun <A> Option<A>.getOrElse(default: @UnsafeVariance A): A =
  when (this) {
    is Option.None -> default
    is Option.Some -> this.value
  }

// ---------- 6.2 ----------

// Kotlin is a strict language: function parameters are evaluated before the function is executed, whether they're needed or not

fun getDefault(): Int = throw RuntimeException()

fun <A> Option<A>.getOrElseLazy(default: A): A = TODO()

fun main() {
  println(Option<Int>())
  println(Option(42))
  println(Option<Int>().getOrElse(42))
  println(listOf<Int>().maybeMax())
  println(listOf(1, 2, 3, 4, 5).maybeMax())
}
