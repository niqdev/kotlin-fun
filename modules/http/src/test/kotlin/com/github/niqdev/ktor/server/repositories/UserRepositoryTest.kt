package com.github.niqdev.ktor.server.repositories

import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserRepositoryTest {

  // singleton pattern
  // https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control
  private val userRepository = UserRepositoryImpl(DatabaseContainer.client)

  // TODO kotest arbitrary
  // https://kotest.io/docs/proptest/property-test-generators.html
  private fun newUser(): User =
    User(
      id = UserId(java.util.UUID.randomUUID()),
      name = "foo-${(0..10).random()}",
      age = (18..40).random()
    )

  @Test
  fun `test repository`() {
    val usersBefore = userRepository.find()
    assertTrue(usersBefore.isSuccess)
    assertTrue(usersBefore.getOrThrow().isEmpty())

    val firstUser = newUser()
    val userId = userRepository.create(firstUser)
    assertTrue(userId.isSuccess)
    assertEquals(1, userId.getOrThrow())

    userRepository.create(newUser())
    userRepository.create(newUser())

    val userById = userRepository.findById(firstUser.id)
    assertTrue(userById.isSuccess)
    assertEquals(firstUser, userById.getOrThrow())

    val allUser = userRepository.find()
    assertTrue(allUser.isSuccess)
    assertEquals(3, allUser.getOrThrow().size)
  }
}
