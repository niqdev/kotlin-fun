package com.github.niqdev.ktor.server.services

import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import com.github.niqdev.ktor.server.routes.UserRequest

interface UserService {
  fun list(): Result<List<User>>
  fun fetch(id: UserId): Result<User>
  fun add(user: UserRequest): Result<UserId>
}

// TODO repositories
class UserServiceImpl : UserService {

  override fun list(): Result<List<User>> =
    Result.success(
      listOf(
        User(UserId(java.util.UUID.randomUUID()), "name1", 1),
        User(UserId(java.util.UUID.randomUUID()), "name2", 2),
        User(UserId(java.util.UUID.randomUUID()), "name3", 3),
      )
    )

  override fun fetch(id: UserId): Result<User> =
    Result.success(User(id, "foo", 42))

  override fun add(user: UserRequest): Result<UserId> =
    Result.success(UserId(java.util.UUID.randomUUID()))
}
