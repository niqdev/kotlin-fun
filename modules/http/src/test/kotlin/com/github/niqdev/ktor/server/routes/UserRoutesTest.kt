package com.github.niqdev.ktor.server.routes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.server.services.UserService
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO https://github.com/skyscreamer/JSONassert
class UserRoutesTest {

  @Test
  fun testGet() = testApplication {
    application {
      val userService = mockk<UserService> {
        every { list() } returns Result.success(emptyList())
      }
      routing { userRoutes(userService) }
    }

    val response = client.get("/user")
    assertEquals(200, response.status.value)
    val users = jacksonObjectMapper().readValue<List<User>>(response.bodyAsText())
    assertEquals(3, users.size)
  }
}
