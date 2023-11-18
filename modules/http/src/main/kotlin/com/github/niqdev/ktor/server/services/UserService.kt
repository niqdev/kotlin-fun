package com.github.niqdev.ktor.server.services

import com.github.niqdev.ktor.models.UserId
import com.github.niqdev.ktor.server.routes.UserRequest

interface UserService {
  fun add(user: UserRequest): Result<UserId>
}

class UserServiceImpl : UserService {
  // TODO
  override fun add(user: UserRequest): Result<UserId> =
    Result.success(UserId(java.util.UUID.randomUUID()))
}
