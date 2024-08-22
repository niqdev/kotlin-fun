package com.github.niqdev.ktor.server.services

import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import com.github.niqdev.ktor.server.repositories.UserRepository
import com.github.niqdev.ktor.server.routes.UserRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserServiceTest {
  @Test
  fun `verify add user`() {
    val repository =
      mockk<UserRepository> {
        every { create(any()) } returns Result.success(8)
      }
    val service = UserServiceImpl(repository)
    val request = UserRequest(name = "foo", age = 42)
    val result = service.add(request)

    assertTrue(result.isSuccess)
  }

  @Test
  fun `verify fetch user`() {
    val userId = UserId(java.util.UUID.randomUUID())
    val user =
      User(
        id = userId,
        name = "foo",
        age = 42,
      )
    val repository =
      mockk<UserRepository> {
        every { findById(userId) } returns Result.success(user)
      }
    val service = UserServiceImpl(repository)
    val result = service.fetch(userId)

    assertTrue(result.isSuccess)
    assertEquals(user, result.getOrThrow())
  }
}
