package com.github.niqdev

sealed class Option<out A> {
  internal object None : Option<Nothing>() {
    override fun toString(): String = "None"
    // override fun equals(other: Any?): Boolean = other === None
    // override fun hashCode(): Int = 0
  }

  internal data class Some<out A>(
    internal val value: A,
  ) : Option<A>() {
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

fun <A> Option<A>.isEmpty(): Boolean =
  when (this) {
    is Option.None -> true
    is Option.Some -> false
  }

fun List<Int>.maxOption(): Option<Int> = Option(this.maxOrNull())

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

fun <A, B> Option<A>.flatMapWithMap(f: (A) -> Option<B>): Option<B> = this.map(f).getOrElse(Option.None)

// ---------- 6.5 ----------

fun <A> Option<A>.orElse(default: () -> Option<A>): Option<A> = Option(this).getOrElseLazy(default)

// ---------- 6.6 ----------

fun <A> Option<A>.filter(p: (A) -> Boolean): Option<A> = this.flatMapWithMap { a -> if (p(a)) Option(a) else Option.None }

// ------------------------------

fun <K, V> Map<K, V>.getOption(key: K): Option<V> = Option(this[key])

// ---------- 6.8 ----------

fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = { it.map(f) }

fun <A, B> ((A) -> B).liftOption(): (Option<A>) -> Option<B> = { it.map(this) }

// ---------- 6.9 ----------

fun <A, B> liftSafe(): ((A) -> B) -> (Option<A>) -> Option<B> =
  { f ->
    { maybeA ->
      try {
        maybeA.map(f)
      } catch (e: Exception) {
        Option.None
      }
    }
  }

fun <A, B> hLift(): ((A) -> B) -> (A) -> Option<B> =
  { f ->
    { a ->
      try {
        Option(a).map(f)
      } catch (e: Exception) {
        Option.None
      }
    }
  }

// ---------- 6.10 ----------

fun <A, B, C> Option<A>.map2(): (Option<B>) -> ((A, B) -> C) -> Option<C> =
  { maybeB -> { f -> this.flatMap { a -> maybeB.map { b -> f(a, b) } } } }

fun <A, B, C, D> Option<A>.map3(): (Option<B>) -> (Option<C>) -> ((A) -> (B) -> (C) -> D) -> Option<D> =
  { maybeB -> { maybeC -> { f -> this.flatMap { a -> maybeB.flatMap { b -> maybeC.map { c -> f(a)(b)(c) } } } } } }

fun main() {
  println(listOf<Int>().maxOption())
  println(listOf(1, 2, 3, 4, 5).maxOption())
  println(Option<Int>())
  println(Option(42))
  println(Option<Int>().getOrElse(42))
  println(Option(42).getOrElseLazy(::getDefault))
  println(Option(42).map { it.toString() })
  println(Option(42).flatMap { int -> Option(int.toString()) })
  println(Option(42).flatMapWithMap { int -> Option(int.toString()) })
  println(Option(42).filter { it < 8 })
  println(mapOf<Int, String>().getOption(1))

  val abs: (Double) -> Double = { d -> if (d > 0) d else -d }
  println(lift(abs)(Option(1.0)))
  println(lift(abs)(Option.None))
  println(lift(String::uppercase)(Option("example")))
  println(abs.liftOption()(Option(1.0)))

  println(Option(42).map2<Int, Int, String>()(Option(8)) { a, b -> "$a | $b" })
}
