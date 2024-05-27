package com.github.niqdev.ktor.server.services

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

interface FileService {
  suspend fun uploadMultipartFile(multipart: MultiPartData, outputPath: String): Result<String>
  suspend fun downloadArchive(inputPath: String, suffix: String): Result<ByteArrayOutputStream>
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

  // https://github.com/ktorio/ktor-samples/blob/main/reverse-proxy/src/main/kotlin/io/ktor/samples/reverseproxy/ReverseProxyApplication.kt
  override suspend fun downloadArchive(inputPath: String, suffix: String): Result<ByteArrayOutputStream> =
    runCatching {
      File(inputPath).listFiles().orEmpty()
        .filter { it.isFile && it.name.endsWith(suffix) }
        .asFlow()
        .map { file ->
          val filePath = "$inputPath/${file.name}"
          log.debug { "archiving $filePath" }
          filePath to file.name
        }
        .let { zipOutputStream(it) }
    }.onFailure {
      log.error { "error downloadArchive $it" }
    }

  // https://stackoverflow.com/questions/46222055/create-a-zip-file-in-kotlin
  private suspend fun zipOutputStream(fileFlow: Flow<Pair<String, String>>): ByteArrayOutputStream =
    ByteArrayOutputStream().use { baos ->
      ZipOutputStream(baos).use { zos ->
        fileFlow.collect { (filePath, fileName) ->
          FileInputStream(filePath).use { fi ->
            BufferedInputStream(fi).use { fileStream ->
              val entry = ZipEntry(fileName)
              zos.putNextEntry(entry)
              fileStream.copyTo(zos, 1024)
            }
          }
        }
      }
      baos
    }
}
