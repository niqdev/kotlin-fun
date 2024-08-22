package com.github.niqdev.ktor.server.routes

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// TODO kotest-assertions-ktor
class StatusRoutesTest {
  @Test
  fun testStatus() =
    testApplication {
      application {
        routing { statusRoutes() }
      }
      client.get("/status").apply {
        assertEquals(HttpStatusCode.OK, status)
        assertEquals("OK", bodyAsText())
      }
    }
}
