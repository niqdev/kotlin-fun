package com.github.niqdev.ktor.server.routes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import com.github.niqdev.ktor.server.repositories.UserRepository
import com.github.niqdev.ktor.server.services.UserService
import com.github.niqdev.ktor.server.services.UserServiceImpl
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

// TODO https://github.com/skyscreamer/JSONassert
class UserRoutesTest {
  @Test
  fun testGetFailure() =
    testApplication {
      application {
        val userService =
          mockk<UserService> {
            every { list() } returns Result.failure(IllegalArgumentException("boom"))
          }
        routing { userRoutes(userService) }
      }

      val response = client.get("/user")
      assertEquals(500, response.status.value)
      assertEquals("Failed to fetch users", response.status.description)
    }

  @Test
  fun testGet() =
    testApplication {
      application {
        val userService =
          mockk<UserService> {
            every { list() } returns
              Result.success(
                listOf(
                  User(UserId(UUID.randomUUID()), "foo", 1),
                  User(UserId(UUID.randomUUID()), "bar", 2),
                ),
              )
          }
        routing { userRoutes(userService) }
      }

      val response = client.get("/user")
      assertEquals(200, response.status.value)
      val users = jacksonObjectMapper().readValue<List<User>>(response.bodyAsText())
      assertEquals(2, users.size)
    }

  @Test
  fun testGetIdInvalid() =
    testApplication {
      application {
        val userRepository = mockk<UserRepository>()
        val userService = UserServiceImpl(userRepository)
        routing { userRoutes(userService) }
      }

      val response = client.get("/user/foo")
      assertEquals(400, response.status.value)
      assertEquals("Invalid id format", response.status.description)
    }

  @Test
  fun testGetIdFailure() =
    testApplication {
      val userId = UserId.fromStringUnsafe("2c9fd82f-040f-4841-b817-52c5152ea273")

      application {
        val userService =
          mockk<UserService> {
            every { fetch(userId) } returns Result.failure(IllegalArgumentException("boom"))
          }
        routing { userRoutes(userService) }
      }

      val response = client.get("/user/${userId.uuid}")
      assertEquals(500, response.status.value)
      assertEquals("Failed to fetch user", response.status.description)
    }

  @Test
  fun testGetId() =
    testApplication {
      val userId = UserId.fromStringUnsafe("2c9fd82f-040f-4841-b817-52c5152ea273")

      application {
        val userService =
          mockk<UserService> {
            every { fetch(userId) } returns
              Result.success(
                User(userId, "bar", 8),
              )
          }
        routing { userRoutes(userService) }
      }

      val response = client.get("/user/${userId.uuid}")
      assertEquals(200, response.status.value)
      assertEquals(
        """
        {
          "id" : "2c9fd82f-040f-4841-b817-52c5152ea273",
          "name" : "bar",
          "age" : 8
        }
        """.trimIndent(),
        response.bodyAsText(),
      )
    }

  @Test
  fun testPostFailure() =
    testApplication {
      application {
        val userService =
          mockk<UserService> {
            every { add(any()) } returns Result.failure(IllegalArgumentException("boom"))
          }
        routing { userRoutes(userService) }
      }

      val client =
        createClient {
          install(ContentNegotiation) {
            jackson()
          }
        }
      val response =
        client.post("/user") {
          contentType(ContentType.Application.Json)
          setBody(UserRequest("foo", 1))
        }
      assertEquals(500, response.status.value)
      assertEquals("Failed to create user", response.status.description)
    }

  @Test
  fun testPost() =
    testApplication {
      val userId = UserId.fromStringUnsafe("2c9fd82f-040f-4841-b817-52c5152ea273")

      application {
        val userService =
          mockk<UserService> {
            every { add(any()) } returns Result.success(userId)
          }
        routing { userRoutes(userService) }
      }

      val client =
        createClient {
          install(ContentNegotiation) {
            jackson()
          }
        }
      val response =
        client.post("/user") {
          contentType(ContentType.Application.Json)
          setBody(UserRequest("foo", 1))
        }
      assertEquals(HttpStatusCode.Created, response.status)
      assertEquals(
        """
        {
          "user_id" : "2c9fd82f-040f-4841-b817-52c5152ea273"
        }
        """.trimIndent(),
        response.bodyAsText(),
      )
    }
}
