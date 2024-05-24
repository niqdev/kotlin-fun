package com.github.niqdev.ktor.server.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

interface FileService {
  suspend fun uploadMultipartFile(multipart: MultiPartData, outputPath: String): Result<String>
}

class FileServiceImpl : FileService {

  private companion object {
    val log = KotlinLogging.logger { }
  }

  override suspend fun uploadMultipartFile(multipart: MultiPartData, outputPath: String): Result<String> =
    runCatching {
      // makes sure output path exists
      Files.createDirectories(Paths.get(outputPath))

      var fileName = ""
      multipart.forEachPart { part ->
        when (part) {
          is PartData.FileItem -> {
            log.debug { "processing multipart data: ${part.name}" }
            fileName = "upload-${System.currentTimeMillis()}-${part.originalFileName}"
            val filePath = "$outputPath/$fileName"
            log.info { "uploading file: $filePath" }

            val file = File(filePath)
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
            log.error { "multipart data not supported: ${part.name}" }
        }
        // make sure to dispose of the part after use to prevent leaks
        part.dispose()
      }
      fileName
    }
}
