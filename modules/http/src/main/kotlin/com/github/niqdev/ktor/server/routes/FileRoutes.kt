package com.github.niqdev.ktor.server.routes

import com.github.niqdev.ktor.server.services.FileService
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveChannel
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import java.io.File

private const val ARCHIVE_PATH = "../../local/archive"

// https://ryanharrison.co.uk/2018/09/20/ktor-file-upload-download.html
fun Route.fileRoutes(fileService: FileService) {
  route("/file") {
    get("/download") {
      val file = File("$ARCHIVE_PATH/kotlin-ktor.png")
      call.response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "ktor.png").toString()
      )
      call.respondFile(file)
    }
    get("/download-archive") {
      // list all png files and return a zip
      fileService.downloadArchive(ARCHIVE_PATH, ".png").fold(
        {
          call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "archive.zip").toString()
          )
          call.respondBytes { it.toByteArray() }
        },
        { call.respond(HttpStatusCode.InternalServerError, "please try again") }
      )
    }
    post("/upload") {
      val file = File("$ARCHIVE_PATH/README.md")
      call.receiveChannel().copyAndClose(file.writeChannel())
      call.respondText("File uploaded")
    }
    post("/upload-multipart") {
      // retrieve all multipart data (suspending)
      val multipart = call.receiveMultipart()
      fileService.uploadMultipartFile(multipart, ARCHIVE_PATH).fold(
        { call.respondText("file uploaded: $it") },
        { call.respond(HttpStatusCode.InternalServerError, "please try again") }
      )
    }
  }
}
