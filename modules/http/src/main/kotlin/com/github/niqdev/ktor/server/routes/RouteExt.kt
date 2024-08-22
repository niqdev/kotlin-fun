package com.github.niqdev.ktor.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.util.pipeline.PipelineContext

internal fun PipelineContext<Unit, ApplicationCall>.onHttpFailure(
  errorMessage: String,
  statusCode: HttpStatusCode = HttpStatusCode.InternalServerError,
): (Throwable) -> Unit =
  { error ->
    call.application.environment.log
      .error(errorMessage, error)
    call.response.status(HttpStatusCode(statusCode.value, errorMessage))
  }
