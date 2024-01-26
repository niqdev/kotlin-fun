package com.github.niqdev.files

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class FileStorageTest : WordSpec({

  val testPath = "src/test/resources/data"
  val testDir = Paths.get(testPath).absolutePathString()

  "FileStorage" should {

    "verify exists" {
      val filePath = FilePath("$testDir/my-file.json")
      val result = PlainFileStorage.exists(filePath)

      result.getOrNull() shouldBe true
    }

    "verify not exists" {
      val filePath = FilePath("$testDir/foo")
      val result = PlainFileStorage.exists(filePath)

      result.getOrNull() shouldBe false
    }
  }
})
