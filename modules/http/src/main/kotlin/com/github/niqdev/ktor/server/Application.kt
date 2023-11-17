package com.github.niqdev.ktor.server

import com.github.niqdev.ktor.server.routes.statusRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

// https://youtrack.jetbrains.com/issue/KTOR-6481/ktor-server-header-validation

// https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/tutorial-server-get-started
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.mainModule() {
  routing {
    statusRoutes()
  }
}
