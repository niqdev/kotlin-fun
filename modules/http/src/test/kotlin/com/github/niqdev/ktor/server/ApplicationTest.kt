package com.github.niqdev.ktor.server

import com.github.niqdev.ktor.server.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO kotest-assertions-ktor
class ApplicationTest {

  @Test
  fun testRoot() = testApplication {
    application {
      configureRouting()
    }
    client.get("/").apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals("Hello Ktor!", bodyAsText())
    }
  }
}
