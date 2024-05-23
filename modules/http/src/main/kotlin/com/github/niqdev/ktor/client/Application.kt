package com.github.niqdev.ktor.client

import com.github.niqdev.ktor.server.routes.UserRequest
import com.sksamuel.hoplite.fp.Validated
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking

private val log = KotlinLogging.logger { }

fun main(args: Array<String>) {

  when (val validatedConfig = ClientConfig.load()) {
    is Validated.Valid -> {
      log.info { "Client config:\n${validatedConfig.value}" }
      runBlocking { runClient(validatedConfig.value) }
    }
    is Validated.Invalid ->
      log.warn { "Invalid client config" }
  }
}

private suspend fun runClient(config: ClientConfig) {
  val client = HttpClient(Java) {
    install(Logging)
    install(HttpRequestRetry) {
      retryOnServerErrors(maxRetries = 5)
      exponentialDelay()
    }
  }
  client.get(config.baseUrl)

  client.post(config.baseUrl) {
    contentType(ContentType.Application.Json)
    setBody(UserRequest(name = "my-name", age = 28))
  }

  // see "use" for single requests
  client.close()
}
