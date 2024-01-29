package com.github.niqdev.ktor.server.routes

import io.ktor.server.application.call
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.homeRoutes() {
  // alternative https://github.com/bkbnio/kompendium
  openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
  swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")

  get("/") {
    call.respondRedirect("/openapi", permanent = true)
  }
}
