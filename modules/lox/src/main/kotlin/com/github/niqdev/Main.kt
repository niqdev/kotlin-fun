package com.github.niqdev

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  when (args.size) {
    1 -> println("runFile")
    0 -> println("runPrompt")
    // https://www.freebsd.org/cgi/man.cgi?query=sysexits
    else -> exitProcess(64)
  }
}

private fun readFile(path: String): String {
  val bytes = Files.readAllBytes(Paths.get(path))
  return String(bytes, Charset.forName(StandardCharsets.UTF_8.name()))
}
