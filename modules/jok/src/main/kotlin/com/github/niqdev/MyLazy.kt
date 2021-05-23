package com.github.niqdev

class MyLazy<out A>(f: () -> A) : () -> A {
  private val value: A by lazy(f)
  override operator fun invoke(): A = value
}

// ---------- 9.4 ----------
// ---------- 9.5 ----------

fun <A, B, C> liftMyLazy2(): ((A) -> (B) -> C) -> (MyLazy<A>) -> (MyLazy<B>) -> MyLazy<C> =
  { eager -> { lazy1 -> { lazy2 -> MyLazy { eager(lazy1())(lazy2()) } } } }

val greetingMessage: (String) -> (String) -> String =
  { greetings -> { name -> "$greetings, $name" } }

// ---------- 9.6 ----------

fun <A, B> MyLazy<A>.map(): ((A) -> B) -> MyLazy<B> =
  { f -> MyLazy { f(this.invoke()) } }

// ---------- 9.7 ----------

fun <A, B> MyLazy<A>.flatMap(): ((A) -> MyLazy<B>) -> MyLazy<B> =
  { f -> f(this.invoke()) }

// ---------- 9.10 ----------

// TODO ???
fun <A> MyLazy<A>.forEach(): (Boolean) -> (() -> Unit) -> (() -> Unit) -> MyLazy<Unit> =
  { condition -> { truthy -> { falsy -> if (condition) MyLazy { truthy() } else MyLazy { falsy() } } } }

fun main() {
  liftMyLazy2<String, String, String>()(greetingMessage)

  println(MyLazy { 42 }.map<Int, String>()() { "value: $it" }.invoke())
  println(MyLazy { 42 }.flatMap<Int, String>()() { MyLazy { "value: $it" } }.invoke())
}
