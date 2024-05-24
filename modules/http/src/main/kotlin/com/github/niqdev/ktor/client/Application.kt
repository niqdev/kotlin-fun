package com.github.niqdev.ktor.client

import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.server.routes.UserRequest
import com.github.niqdev.ktor.server.routes.UserResponse
import com.sksamuel.hoplite.fp.Validated
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking

private val log = KotlinLogging.logger { }

private enum class Argument {
  ARG_USER,
  ARG_DOWNLOAD,
  ARG_UPLOAD;
}

fun main(args: Array<String>) {
  when (val validatedConfig = ClientConfig.load()) {
    is Validated.Valid -> {
      log.info { "Client config:\n${validatedConfig.value}" }
      runClient(args.toList(), validatedConfig.value)
    }
    is Validated.Invalid ->
      log.warn { "Invalid client config:\n${validatedConfig.error.description()}" }
  }
}

private fun runClient(args: List<String>, config: ClientConfig) {
  log.debug { "arguments: $args" }
  if (args.size != 1) {
    log.error { "invalid arguments: ${Argument.entries}" }
    return
  }

  when (Argument.valueOf(args.first().uppercase())) {
    Argument.ARG_USER ->
      runBlocking { runUserClient(config) }
    Argument.ARG_DOWNLOAD ->
      TODO()
    Argument.ARG_UPLOAD ->
      TODO()
  }
}

private suspend fun runUserClient(config: ClientConfig) {
  val client = HttpClientBuilder.build()
  val usersBefore = client.get("${config.baseUrl}/user").body<List<User>>()
  log.info { "Users before: $usersBefore" }

  val newUser = client.post("${config.baseUrl}/user") {
    contentType(ContentType.Application.Json)
    setBody(UserRequest(name = "my-name", age = 28))
  }.body<UserResponse>()
  log.info { "New user: $newUser" }

  // see "use" for single requests
  client.close()
}
