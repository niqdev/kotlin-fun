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

fun <A> Option<A>.getOrElseLazy(default: () -> A): A =
  when (this) {
    is Option.None -> default()
    is Option.Some -> this.value
  }

// ---------- 6.3 ----------

fun <A, B> Option<A>.map(f: (A) -> B): Option<B> =
  when (this) {
    is Option.None -> Option.None
    is Option.Some -> Option(f(this.value))
  }

// ---------- 6.4 ----------

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> =
  when (this) {
    is Option.None -> Option.None
    is Option.Some -> f(this.value)
  }

fun <A, B> Option<A>.flatMapWithMap(f: (A) -> Option<B>): Option<B> =
  this.map(f).getOrElse(Option.None)

// ---------- 6.5 ----------

fun <A> Option<A>.orElse(default: () -> Option<A>): Option<A> =
  Option(this).getOrElseLazy(default)

// ---------- 6.6 ----------

fun <A> Option<A>.filter(p: (A) -> Boolean): Option<A> =
  this.flatMapWithMap { a -> if (p(a)) Option(a) else Option.None }

// ------------------------------

fun <K, V> Map<K, V>.getOption(key: K): Option<V> =
  Option(this[key])

fun main() {
  println(listOf<Int>().maybeMax())
  println(listOf(1, 2, 3, 4, 5).maybeMax())
  println(Option<Int>())
  println(Option(42))
  println(Option<Int>().getOrElse(42))
  println(Option(42).getOrElseLazy(::getDefault))
  println(Option(42).map { it.toString() })
  println(Option(42).flatMap { int -> Option(int.toString()) })
  println(Option(42).flatMapWithMap { int -> Option(int.toString()) })
  println(Option(42).filter { it < 8 })
  println(mapOf<Int, String>().getOption(1))
}