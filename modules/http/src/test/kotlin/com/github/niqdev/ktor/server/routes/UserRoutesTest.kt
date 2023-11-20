package com.github.niqdev.ktor.server.routes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import com.github.niqdev.ktor.server.services.UserService
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO https://github.com/skyscreamer/JSONassert
class UserRoutesTest {

  @Test
  fun testGet() = testApplication {
    application {
      val userService = mockk<UserService> {
        every { list() } returns Result.success(
          listOf(
            User(UserId(UUID.randomUUID()), "foo", 1),
            User(UserId(UUID.randomUUID()), "bar", 2),
          )
        )
      }
      routing { userRoutes(userService) }
    }

    val response = client.get("/user")
    assertEquals(200, response.status.value)
    val users = jacksonObjectMapper().readValue<List<User>>(response.bodyAsText())
    assertEquals(2, users.size)
  }
}
