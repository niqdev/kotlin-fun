package com.github.niqdev.utils

import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import org.slf4j.LoggerFactory
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.absolutePathString

/*
 * https://commons.apache.org/proper/commons-compress/examples.html
 * https://stackoverflow.com/questions/13461393/compress-directory-to-tar-gz-with-commons-compress
 * https://stackoverflow.com/questions/11431143/how-to-untar-a-tar-file-using-apache-commons
 * https://mkyong.com/java/how-to-create-tar-gz-in-java
 *
 * # compress archive
 * tar -zcvf local/archive/example.tar.gz  local/archive/example
 *
 * # list archive content
 * tar -tvf local/archive/example.tar.gz
 *
 * # decompress archive
 * tar -zxvf local/archive/example.tar.gz
 */
object TarUtils {
  private val log = LoggerFactory.getLogger(TarUtils::class.java)
  private val BLACKLIST_FILES = listOf("._", ".DS_Store")

  fun compress(source: String, outputPrefix: String): String {
    val outputPath = Paths.get(source)
    val outputFile = Files.createTempFile(outputPrefix, ".tar.gz")
    log.info("Compressing outputPath=$outputPath outputFile=${outputFile.absolutePathString()}")

    TarArchiveOutputStream(GzipCompressorOutputStream(BufferedOutputStream(Files.newOutputStream(outputFile)))).use { tOut ->
      tOut.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR)
      tOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU)
      tOut.setAddPaxHeadersForNonAsciiNames(true)

      Files.walkFileTree(
        outputPath,
        object : SimpleFileVisitor<Path>() {
          override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult =
            runCatching {
              val targetFile = outputPath.relativize(file)
              log.debug("Archiving $targetFile")

              val tarEntry = TarArchiveEntry(file.toFile(), targetFile.toString())
              tOut.putArchiveEntry(tarEntry)
              Files.copy(file, tOut)
              tOut.closeArchiveEntry()
            }
              .onFailure { log.warn("Internal error archiving $file", it) }
              .run { FileVisitResult.CONTINUE }

          override fun visitFileFailed(file: Path, error: IOException): FileVisitResult {
            log.error("Error archiving $file", error)
            return FileVisitResult.CONTINUE
          }
        }
      )
      tOut.finish()
    }

    return outputFile.absolutePathString()
  }

  fun decompress(archivePath: String, outputPrefix: String): String {
    val temporaryPath = Files.createTempDirectory(outputPrefix)
    log.info("Decompressing archivePath=$archivePath temporaryPath=${temporaryPath.absolutePathString()}")

    TarArchiveInputStream(GzipCompressorInputStream(BufferedInputStream(FileInputStream(archivePath)))).use { tIn ->
      var currentEntry = tIn.nextEntry
      while (currentEntry != null) {
        log.debug("Extracting entry=${currentEntry.name}")

        if (BLACKLIST_FILES.any { currentEntry.name.contains(it) }) {
          log.debug("Skipping blacklisted entry=${currentEntry.name}")
          currentEntry = tIn.nextEntry
          continue
        }

        val currentFile = File(temporaryPath.toFile(), currentEntry.name)
        if (currentEntry.isDirectory()) {
          if (!currentFile.isDirectory && !currentFile.mkdirs()) {
            throw IOException("failed to create directory $currentFile")
          }
        } else {
          val parent = currentFile.getParentFile()
          if (!parent.isDirectory && !parent.mkdirs()) {
            throw IOException("failed to create parent directory $parent")
          }
          Files.copy(tIn, currentFile.toPath())
        }
        currentEntry = tIn.nextEntry
      }
    }
    return temporaryPath.absolutePathString()
  }

  fun addFileToArchive(archivePath: String, info: FileInfo, deleteTmpDir: Boolean = false): Result<String> =
    runCatching {
      val temporaryPath = decompress(archivePath, info.prefix)

      log.debug("Adding file=${info.name} to temporaryPath=$temporaryPath")
      // writes file to disk
      File(temporaryPath, info.name).writeText(info.data)

      val outputPath = compress(temporaryPath, info.prefix)
      if (deleteTmpDir) {
        val deleted = File(temporaryPath).deleteRecursively()
        log.debug("Deleting temporaryPath=$temporaryPath deleted=$deleted")
      }

      outputPath
    }
}

data class FileInfo(val name: String, val prefix: String, val data: String)
