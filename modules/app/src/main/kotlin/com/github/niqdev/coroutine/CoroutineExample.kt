package com.github.niqdev.coroutine

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// https://kotlinlang.org/docs/coroutines-guide.html
// a `coroutine` is an instance of suspendable computation and is not bound to any particular thread
// principle of `structured concurrency` which means that new coroutines can be only launched in a specific `CoroutineScope` e.g. runBlocking

// to debug use JVM option: -Dkotlinx.coroutines.debug

// PRINTS
/*
coroutine
my
World!
The answer is 55
Completed in 2002 ms
The answer is 3
Hello
"." x 100k
*/

fun main() =
  // runs a new coroutine and blocks the current thread until its completion
  runBlocking {

    // ------------------------------

    // launch a new coroutine and continue - depends on CoroutineScope
    launch {
      // it suspends the coroutine for N second (millis) without blocking the underlying thread
      delay(2000L)
      println("my")
      printWorld()
    }

    // ------------------------------

    // both pieces of code inside launch { ... } blocks execute concurrently
    myScope()

    // ------------------------------

    // it launches 100K coroutines and, after 5 seconds, each coroutine prints a dot
    repeat(100_000) {
      launch {
        delay(5000L)
        print(".")
      }
    }

    // ------------------------------

    val time = sequential()
    println("Completed in $time ms")

    // ------------------------------

    parallel()

    // ------------------------------

    // main coroutine continues
    println("Hello")
  }

// ------------------------------

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

// ------------------------------

// the code in the coroutine, just like in the regular code, is "sequential" by default

suspend fun doSomethingUseful(time: Long, result: Int): Int {
  delay(1000L)
  return result
}

suspend fun sequential() = measureTimeMillis {
  val one = doSomethingUseful(1000L, 13)
  val two = doSomethingUseful(1000L, 42)
  println("The answer is ${one + two}")
}

// ------------------------------

// `launch` returns a `Job` and does not carry any resulting value, while `async` returns a `Deferred` — a light-weight non-blocking future that represents a promise to provide a result later

suspend fun parallel() = coroutineScope {
  val one = async { doSomethingUseful(1000L, 1) }
  val two = async(start = CoroutineStart.LAZY) { doSomethingUseful(1000L, 2) }
  delay(500L)
  two.start()
  println("[${Thread.currentThread().name}] The answer is ${one.await() + two.await()}")
}
