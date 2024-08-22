package com.github.niqdev.ktor.server.routes

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VersionRoutesTest {
  @Test
  fun testVersionWithoutHeader() =
    testApplication {
      application {
        routing { versionRoutes() }
      }

      val response = client.get("/version")
      assertEquals(HttpStatusCode.BadRequest, response.status)
      assertEquals("Required header is missing", response.bodyAsText())
    }

  @Test
  fun testVersion() =
    testApplication {
      application {
        routing { versionRoutes() }
      }

      val response =
        client.get("/version") {
          header("X-My-Version", "foo")
        }
      assertEquals("foo", response.bodyAsText())
    }
}
