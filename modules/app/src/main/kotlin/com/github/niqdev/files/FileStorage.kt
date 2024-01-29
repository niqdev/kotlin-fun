package com.github.niqdev.files

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class FilePath(val value: String) {
  fun toUnsafePath(): Path = Paths.get(value)
}

sealed interface FileStorage<T> {
  fun exists(filePath: FilePath): FileResult<Boolean>
  fun get(filePath: FilePath): FileResult<T>
  fun list(prefix: String, suffix: String): FileResult<T>
  fun store(filePath: FilePath, value: T): FileResult<Unit>
  fun delete(filePath: FilePath): FileResult<Unit>
}

data object PlainFileStorage : FileStorage<String> {

  override fun exists(filePath: FilePath): FileResult<Boolean> =
    runCatching { Files.exists(filePath.toUnsafePath()) }.toFileResult()

  override fun get(filePath: FilePath): FileResult<String> =
    runCatching { Files.readString(filePath.toUnsafePath()) }.toFileResult()

  override fun list(prefix: String, suffix: String): FileResult<String> {
    TODO("Not yet implemented")
  }

  override fun delete(filePath: FilePath): FileResult<Unit> {
    TODO("Not yet implemented")
  }

  override fun store(filePath: FilePath, value: String): FileResult<Unit> {
    TODO("Not yet implemented")
  }
}
