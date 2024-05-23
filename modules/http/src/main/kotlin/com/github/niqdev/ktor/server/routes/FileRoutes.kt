package com.github.niqdev.ktor.server.routes

import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.server.application.call
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import java.io.File

fun Route.fileRoutes() {
  route("/file") {
    get("/download") {
      val file = File("../../local/archive/kotlin-ktor.png")
      call.response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "ktor.png")
          .toString()
      )
      call.respondFile(file)
    }
    post("/upload") {
      val file = File("../../local/archive/README.md")
      call.receiveChannel().copyAndClose(file.writeChannel())
      call.respondText("File Uploaded")
    }
  }
}
