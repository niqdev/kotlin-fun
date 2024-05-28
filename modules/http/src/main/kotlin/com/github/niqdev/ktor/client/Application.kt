package com.github.niqdev.ktor.client

import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.server.routes.UserRequest
import com.github.niqdev.ktor.server.routes.UserResponse
import com.sksamuel.hoplite.fp.Validated
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import java.io.File

private val log = KotlinLogging.logger { }

private enum class Argument {
  ARG_USER,
  ARG_UPLOAD,
  ARG_DOWNLOAD;
}
private const val ARCHIVE_PATH = "../../local/archive"

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
    Argument.ARG_UPLOAD ->
      runBlocking { runUploadClient(config) }
    Argument.ARG_DOWNLOAD ->
      runBlocking { runDownloadClient(config) }
  }
}

private suspend fun runUserClient(config: ClientConfig) {
  val client = HttpClientBuilder.buildJson()
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

private suspend fun runUploadClient(config: ClientConfig) {
  val client = HttpClientBuilder.build()

  val response = client.post("${config.baseUrl}/file/upload-multipart") {
    setBody(
      MultiPartFormDataContent(
        formData {
          append("description", "ktor logo")
          append(
            "image", File("$ARCHIVE_PATH/kotlin-ktor.png").readBytes(),
            Headers.build {
              // see ContentType.Image.PNG
              append(HttpHeaders.ContentType, "image/png")
              append(HttpHeaders.ContentDisposition, "filename=\"kotlin-ktor.png\"")
            }
          )
        }
      )
    )
    onUpload { bytesSentTotal, contentLength ->
      log.info { "Sent $bytesSentTotal bytes from $contentLength" }
    }
  }

  log.info(response.bodyAsText())
}

// alternative https://github.com/ktorio/ktor/issues/1639
private suspend fun runDownloadClient(config: ClientConfig) {
  val client = HttpClientBuilder.build()

  val response = client.get("${config.baseUrl}/file/download-archive") {
    onDownload { bytesSentTotal, contentLength ->
      log.debug { "Received $bytesSentTotal bytes from $contentLength" }
    }
  }

  val fileName = ContentDisposition
    .parse(response.headers[HttpHeaders.ContentDisposition].orEmpty())
    .parameter(ContentDisposition.Parameters.FileName)

  val file = File("$ARCHIVE_PATH/download-${System.currentTimeMillis()}-$fileName")
  file.writeBytes(response.body())
  log.info { "Downloaded ${file.path}" }
}
