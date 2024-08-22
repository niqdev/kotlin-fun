package com.github.niqdev.files

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class FilePath(
  val value: String,
) {
  fun toUnsafePath(): Path = Paths.get(value)
}

sealed interface FileStorage<T> {
  fun exists(filePath: FilePath): FileResult<Boolean>

  fun get(filePath: FilePath): FileResult<T>

  fun list(
    prefix: String,
    suffix: String,
  ): FileResult<List<FilePath>>

  fun store(
    filePath: FilePath,
    value: T,
  ): FileResult<Unit>

  fun delete(filePath: FilePath): FileResult<Unit>
}

data object PlainFileStorage : FileStorage<String> {
  override fun exists(filePath: FilePath): FileResult<Boolean> = runCatching { Files.exists(filePath.toUnsafePath()) }.toFileResult()

  override fun get(filePath: FilePath): FileResult<String> = runCatching { Files.readString(filePath.toUnsafePath()) }.toFileResult()

  override fun list(
    prefix: String,
    suffix: String,
  ): FileResult<List<FilePath>> =
    runCatching {
      Files
        .newDirectoryStream(Paths.get(prefix), "*$suffix")
        .mapNotNull { FilePath(it.toFile().absolutePath.removePrefix("$prefix/")) }
    }.toFileResult()

  override fun store(
    filePath: FilePath,
    value: String,
  ): FileResult<Unit> =
    runCatching {
      Files.createDirectories(filePath.toUnsafePath().parent)
      Files.write(filePath.toUnsafePath(), value.toByteArray())
      Unit
    }.toFileResult()

  override fun delete(filePath: FilePath): FileResult<Unit> =
    runCatching {
      Files.deleteIfExists(filePath.toUnsafePath())
      Unit
    }.toFileResult()
}
