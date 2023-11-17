package com.github.niqdev.ktor.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.Route

fun Route.statusRoutes() {
  get("/status") {
    call.respondText("OK", status = HttpStatusCode.OK)
  }
}
