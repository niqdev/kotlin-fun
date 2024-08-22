package com.github.niqdev.utils

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class TarTest :
  WordSpec({

    "TarUtils" should {
      "decompress and compress" {
        // val currentWorkingDirectory = FileSystems.getDefault().getPath("").absolutePathString()

        val archivePath = "../../local/archive/example.tar.gz"
        val tmpPath = TarUtils.decompress(archivePath, "")
        val filesBefore = listRelativeFiles(tmpPath)
        val expectedBefore =
          listOf(
            "/local",
            "/local/archive",
            "/local/archive/example",
            "/local/archive/example/README.txt",
            "/local/archive/example/bin",
            "/local/archive/example/bin/binary.txt",
          )
        filesBefore.size shouldBe 6
        expectedBefore shouldBe filesBefore.sorted()

        val fileInfo = FileInfo(name = "foo.txt", prefix = "", data = "TODO")
        val result = TarUtils.addFileToArchive(archivePath, fileInfo, false)
        result.isSuccess shouldBe true

        val outputPath = TarUtils.decompress(result.getOrThrow(), "")
        val filesAfter = listRelativeFiles(outputPath)
        val expectedAfter =
          listOf(
            "/foo.txt",
            "/local",
            "/local/archive",
            "/local/archive/example",
            "/local/archive/example/README.txt",
            "/local/archive/example/bin",
            "/local/archive/example/bin/binary.txt",
          )
        filesAfter.size shouldBe 7
        expectedAfter shouldBe filesAfter.sorted()

        val addedData = File("$outputPath/${fileInfo.name}").readText()
        fileInfo.data shouldBe addedData
      }
    }
  })

private fun listRelativeFiles(path: String): List<String> {
  val files = Files.walk(Paths.get(path)).map { it.absolutePathString() }.toList()
  return files.map { it.removePrefix(files.first()) }.drop(1)
}
