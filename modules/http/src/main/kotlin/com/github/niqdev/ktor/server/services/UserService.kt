package com.github.niqdev.ktor.server.services

import com.github.niqdev.ktor.common.flatMap
import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import com.github.niqdev.ktor.server.repositories.UserRepository
import com.github.niqdev.ktor.server.routes.UserRequest

interface UserService {
  fun add(request: UserRequest): Result<UserId>

  fun fetch(id: UserId): Result<User>

  fun list(): Result<List<User>>
}

class UserServiceImpl(
  private val repository: UserRepository,
) : UserService {
  override fun add(request: UserRequest): Result<UserId> {
    val user = toUser(request)
    return repository.create(user).map { user.id }
  }

  override fun fetch(id: UserId): Result<User> =
    repository.findById(id).flatMap { user ->
      if (user != null) {
        Result.success(user)
      } else {
        Result.failure(IllegalArgumentException("user not found"))
      }
    }

  override fun list(): Result<List<User>> = repository.find()

  private fun toUser(request: UserRequest): User =
    User(
      id = UserId(java.util.UUID.randomUUID()),
      name = request.name,
      age = request.age,
    )
}
