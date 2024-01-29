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

      result.isSuccess() shouldBe true
      result.getOrNull() shouldBe true
    }

    "verify invalid exists" {
      val filePath = FilePath("$testDir/foo")
      val result = PlainFileStorage.exists(filePath)

      result.isSuccess() shouldBe true
      result.getOrNull() shouldBe false
    }

    "verify get" {
      val filePath = FilePath("$testDir/my-file.json")
      val result = PlainFileStorage.get(filePath)

      result.isSuccess() shouldBe true
      result.getOrNull() shouldBe "{}"
    }

    "verify invalid get" {
      val filePath = FilePath("$testDir/foo")
      val result = PlainFileStorage.get(filePath)

      // TODO FileFailure.FileNotFound
      result.isSuccess() shouldBe false
    }
  }
})
