package com.github.niqdev.ktor.server.services

import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import com.github.niqdev.ktor.server.repositories.UserRepository
import com.github.niqdev.ktor.server.routes.UserRequest

interface UserService {
  fun add(request: UserRequest): Result<UserId>
  fun fetch(id: UserId): Result<User>
  fun list(): Result<List<User>>
}

class UserServiceImpl(private val repository: UserRepository) : UserService {

  override fun list(): Result<List<User>> =
    repository.find()

  override fun fetch(id: UserId): Result<User> =
    repository.findById(id)

  override fun add(request: UserRequest): Result<UserId> {
    val user = toUser(request)
    return repository.create(user).map { user.id }
  }

  private fun toUser(request: UserRequest): User =
    User(
      id = UserId(java.util.UUID.randomUUID()),
      name = request.name,
      age = request.age
    )
}
