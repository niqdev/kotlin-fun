package com.github.niqdev.coroutine

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// https://kotlinlang.org/docs/coroutines-guide.html
// a `coroutine` is an instance of suspendable computation and is not bound to any particular thread
// principle of `structured concurrency` which means that new coroutines can be only launched in a specific `CoroutineScope` e.g. runBlocking

// PRINTS
// coroutine
// Hello
// my
// World!
// "." x 100k
fun main() =
  // runs a new coroutine and blocks the current thread until its completion
  runBlocking {
    // launch a new coroutine and continue - depends on CoroutineScope
    launch {
      // it suspends the coroutine for N second (millis) without blocking the underlying thread
      delay(2000L)
      println("my")
      printWorld()
    }
    // both pieces of code inside launch { ... } blocks execute concurrently
    myScope()

    // it launches 100K coroutines and, after 5 seconds, each coroutine prints a dot
    repeat(100_000) {
      launch {
        delay(5000L)
        print(".")
      }
    }

    // main coroutine continues
    println("Hello")
  }

// `suspending functions` can be used inside coroutines just like regular functions,
// but their additional feature is that they can, in turn, use other suspending functions to suspend execution of a coroutine

suspend fun printWorld() {
  delay(1000L)
  println("World!")
}

suspend fun myScope() = coroutineScope {
  launch {
    delay(1000L)
    println("coroutine")
  }
}
