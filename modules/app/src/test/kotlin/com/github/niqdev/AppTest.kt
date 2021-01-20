package com.github.niqdev

import kotlin.test.Test
import kotlin.test.assertEquals

class AppTest {

  @Test
  fun testAppHasAGreeting() {
    val classUnderTest = App()
    assertEquals(classUnderTest.greeting, "Hello World!", "app should have a greeting")
  }
}
