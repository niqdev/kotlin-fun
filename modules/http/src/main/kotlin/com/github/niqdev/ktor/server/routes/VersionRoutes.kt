package com.github.niqdev.ktor.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.hooks.CallSetup
import io.ktor.server.application.install
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

private const val VERSION_HEADER = "X-My-Version"

// HEADER https://youtrack.jetbrains.com/issue/KTOR-6481/ktor-server-header-validation
// https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/create-application-plugin.html
// https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/create-route-scoped-plugin.html
private val myVersionPlugin = createRouteScopedPlugin("MyVersionPlugin") {
  on(CallSetup) { call ->
    if (!call.request.headers.contains(VERSION_HEADER)) {
      throw IllegalArgumentException("Required header is missing")
    }
  }
}

fun Route.versionRoutes() {
  route("/version") {
    install(myVersionPlugin)
    get {
      val myVersionHeader = call.request.headers[VERSION_HEADER].orEmpty()
      call.respondText(myVersionHeader, status = HttpStatusCode.OK)
    }
  }
}
