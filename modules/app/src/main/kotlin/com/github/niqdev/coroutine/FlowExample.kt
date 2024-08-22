package com.github.niqdev.coroutine

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// https://kotlinlang.org/docs/flow.html

// a `flow` is a stream of values that are being asynchronously computed
// Flows are `cold` streams: does not run until the flow is collected
// Flow is a reactive stream

fun main() =
  runBlocking {
    // ------------------------------

    launch {
      for (k in 1..3) {
        println("$k not blocked")
        delay(90)
      }
    }
    simple().collect(::println)

    // ------------------------------

    responses.collect(::println)
  }

// not suspended
fun simple(): Flow<Int> =
  flow {
    for (i in 1..3) {
      delay(100)
      emit(i)
    }
  }

// ------------------------------

suspend fun request(value: Int): String {
  delay(1000)
  return "response: $value"
}

val responses = (1..3).asFlow().map(::request)
