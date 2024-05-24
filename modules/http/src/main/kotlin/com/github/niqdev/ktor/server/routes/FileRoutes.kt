package com.github.niqdev.ktor.server.routes

import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.content.*
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.request.receiveChannel
import io.ktor.server.request.receiveMultipart
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

private const val ARCHIVE_PATH = "../../local/archive"

// https://ryanharrison.co.uk/2018/09/20/ktor-file-upload-download.html
fun Route.fileRoutes() {
  route("/file") {
    get("/download") {
      val file = File("$ARCHIVE_PATH/kotlin-ktor.png")
      call.response.header(
        HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "ktor.png")
          .toString()
      )
      call.respondFile(file)
    }
    get("/download-archive") {
      TODO()
    }
    post("/upload") {
      val file = File("$ARCHIVE_PATH/README.md")
      call.receiveChannel().copyAndClose(file.writeChannel())
      call.respondText("File Uploaded")
    }
    post("/upload-multipart") {
      // retrieve all multipart data (suspending)
      val multipart = call.receiveMultipart()
      var fileName = ""
      multipart.forEachPart { part ->
        when (part) {
          is PartData.FileItem -> {
            this.application.log.debug("processing multipart data name=${part.name}")
            fileName = "upload-${System.currentTimeMillis()}-${part.originalFileName}"
            val file = File("$ARCHIVE_PATH/$fileName")
            // use InputStream from part to save file
            part.streamProvider().use { its ->
              // copy the stream to the file with buffering
              file.outputStream().buffered().use {
                // note that this is blocking
                its.copyTo(it)
              }
            }
          }
          else ->
            this.application.log.error("multipart data not supported name=${part.name}")
        }
        // make sure to dispose of the part after use to prevent leaks
        part.dispose()
      }
      call.respondText("File Uploaded: $fileName")
    }
  }
}
