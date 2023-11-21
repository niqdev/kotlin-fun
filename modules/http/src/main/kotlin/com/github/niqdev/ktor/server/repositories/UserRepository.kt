package com.github.niqdev.ktor.server.repositories

import com.github.niqdev.ktor.models.User

// TODO
interface UserRepository {
  fun create(): Result<Int>
  fun findById(): Result<User>
  fun find(): Result<List<User>>
}
