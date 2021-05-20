package com.github.niqdev.arrow

import arrow.core.Either
import org.junit.Test
import kotlin.test.assertEquals

class ArrowTest {

  @Test
  fun testEither() {
    val value = Either.conditionally(true, { "Error" }, { 42 })
    val expected = Either.Right(42)
    assertEquals(value, expected)
  }
}
