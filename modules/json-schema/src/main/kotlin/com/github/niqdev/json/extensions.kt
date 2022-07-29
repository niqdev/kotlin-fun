package com.github.niqdev.json

fun String.jsonToString(): String =
  object {}.javaClass.getResource(this)?.readText() ?: "INVALID_PATH"
