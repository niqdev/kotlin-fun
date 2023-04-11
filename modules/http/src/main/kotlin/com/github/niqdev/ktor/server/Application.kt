package com.github.niqdev.ktor.server

import com.github.niqdev.ktor.server.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

// https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/tutorial-server-get-started
fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
    .start(wait = true)
}

fun Application.module() {
  configureRouting()
}

// TODO json + client + docker + chart
