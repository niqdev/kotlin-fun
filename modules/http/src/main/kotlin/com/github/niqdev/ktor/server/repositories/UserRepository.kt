package com.github.niqdev.ktor.server.repositories

import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import kotlin.jvm.optionals.getOrNull

interface UserRepository {
  fun create(user: User): Result<Int>
  fun findById(id: UserId): Result<User>
  fun find(): Result<List<User>>
}

class UserRepositoryImpl(private val client: Jdbi) : UserRepository {

  companion object {
    private const val TABLE_NAME = "users"
    private val log = KotlinLogging.logger {}
  }

  override fun create(user: User): Result<Int> =
    runCatching {
      log.debug { "create user: $user" }

      client.withHandle<Int, Exception> { handle ->
        handle.createUpdate("INSERT INTO $TABLE_NAME (id, name, age) VALUES (:id, :name, :age);")
          .bind("id", user.id.uuid.toString())
          .bind("name", user.name)
          .bind("age", user.age)
          .execute()
      }
    }

  override fun findById(id: UserId): Result<User> =
    runCatching {
      log.debug { "find user by id: $id" }

      client.withHandle<User, Exception> { handle ->
        handle.select("SELECT * FROM $TABLE_NAME WHERE id = :id;")
          .bind("id", id.uuid.toString())
          .map(::toUser)
          .findOne()
          .getOrNull()
      }
    }

  override fun find(): Result<List<User>> =
    runCatching {
      log.debug { "find users" }

      client.withHandle<List<User>, Exception> { handle ->
        handle.select("SELECT * FROM $TABLE_NAME")
          .map(::toUser)
          .list()
      }
    }

  private fun toUser(rs: ResultSet, ctx: StatementContext): User =
    User(
      id = UserId.fromStringUnsafe(rs.getString("id")),
      name = rs.getString("name"),
      age = rs.getInt("age")
    )
}
