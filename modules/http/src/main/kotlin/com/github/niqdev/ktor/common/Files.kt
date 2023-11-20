package com.github.niqdev.ktor.common

object Files {

  fun unsafeReadFromResource(path: String): String =
    this::class.java.classLoader.getResource(path)?.readText()!!

  fun readFromResource(path: String): Result<String> =
    runCatching { unsafeReadFromResource(path) }

  fun unsafeReadFromFile(path: String): String =
    java.io.File(path).readText()

  fun readFromFile(path: String): Result<String> =
    runCatching { unsafeReadFromFile(path) }
}
