package com.github.niqdev.ktor.server.routes

import com.github.niqdev.ktor.models.UserId
import com.github.niqdev.ktor.server.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

data class UserRequest(val name: String, val age: Int)

// TODO use instead of RESULT throwable
sealed interface UserResponseError {
  data object InvalidRequest
  data object InternalError
}

fun Route.userRoutes(userService: UserService) {
  route("/user") {
    get {
      val onFailure: (Throwable) -> Unit = { error ->
        val errorMessage = "Failed to fetch users"
        call.application.environment.log.error(errorMessage, error)
        call.response.status(HttpStatusCode(HttpStatusCode.InternalServerError.value, errorMessage))
      }
      userService.list().fold({ call.respond(it) }, onFailure)
    }
    get("/{id}") {
      val idParameter = call.parameters["id"].orEmpty()
      UserId.fromString(idParameter)
        .onFailure {
          val errorMessage = "Invalid id format"
          call.application.environment.log.error(errorMessage, it)
          call.response.status(HttpStatusCode(HttpStatusCode.BadRequest.value, errorMessage))
        }
        .onSuccess {
          val onFailure: (Throwable) -> Unit = { error ->
            val errorMessage = "Failed to fetch user"
            call.application.environment.log.error(errorMessage, error)
            call.response.status(HttpStatusCode(HttpStatusCode.InternalServerError.value, errorMessage))
          }
          userService.fetch(it).fold({ user -> call.respond(user) }, onFailure)
        }
    }
    post {
      val userRequest = call.receive<UserRequest>()
      val onSuccess: (UserId) -> Unit = {
        call.response.status(HttpStatusCode(HttpStatusCode.Created.value, it.uuid.toString()))
      }
      val onFailure: (Throwable) -> Unit = {
        val errorMessage = "Failed to create user"
        call.application.environment.log.error(errorMessage, it)
        call.response.status(HttpStatusCode(HttpStatusCode.InternalServerError.value, errorMessage))
      }
      userService.add(userRequest).fold(onSuccess, onFailure)
    }
  }
}
