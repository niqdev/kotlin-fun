package com.github.niqdev.json

fun String.readFromResource(): String =
  object {}.javaClass.getResource(this)?.readText() ?: "INVALID_PATH"

private fun String.readFromFile(): String =
  java.io.File(this).readText()
