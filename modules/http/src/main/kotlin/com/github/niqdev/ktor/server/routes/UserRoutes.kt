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
      call.respond(userService.list())
    }
    get("/{id}") {
      val idParameter = call.parameters["id"].orEmpty()
      UserId.fromString(idParameter)
        .onFailure {
          val errorMessage = "Invalid id"
          call.application.environment.log.error(errorMessage, it)
          call.response.status(HttpStatusCode(HttpStatusCode.BadRequest.value, errorMessage))
        }
        .onSuccess {
          call.respond(userService.get(it))
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
