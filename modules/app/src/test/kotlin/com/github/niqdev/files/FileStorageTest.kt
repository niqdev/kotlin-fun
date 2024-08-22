package com.github.niqdev.files

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class FileStorageTest :
  WordSpec({

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

      "verify list" {
        val result = PlainFileStorage.list(testDir, "txt")

        result.isSuccess() shouldBe true
        val paths = result.getOrNull()
        paths?.size shouldBe 2
        val expected = listOf("my-file-1.txt", "my-file-2.txt").map { FilePath(it) }
        paths?.sortedBy { it.value } shouldBe expected
      }

      "verify store and delete" {
        val filePath = FilePath("$testDir/my-example")
        val fileValue = "foo"

        val storeResult = PlainFileStorage.store(filePath, fileValue)
        storeResult.isSuccess() shouldBe true

        PlainFileStorage.get(filePath).getOrNull() shouldBe fileValue
        PlainFileStorage.exists(filePath).getOrNull() shouldBe true

        val deleteResult = PlainFileStorage.delete(filePath)
        deleteResult.isSuccess() shouldBe true
        PlainFileStorage.exists(filePath).getOrNull() shouldBe false
      }
    }
  })
