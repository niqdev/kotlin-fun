package com.github.niqdev

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

object Lox {

  private var error = false

  @JvmStatic
  fun main(args: Array<String>) {
    println("Current working directory: ${System.getProperty("user.dir")}")
    println(">>> ${args[0]}")

    when (args.size) {
      1 -> run(readFile(args[0]))
      0 -> println("runPrompt")
      // https://www.freebsd.org/cgi/man.cgi?query=sysexits
      else -> exitProcess(64)
    }
  }

  fun readFile(path: String): String {
    val bytes = Files.readAllBytes(Paths.get(path))
    return String(bytes, Charset.forName(StandardCharsets.UTF_8.name()))
  }

  // CTRL+D to interrupt
  private fun runPrompt() {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
      print("> ")
      reader.readLine()
      error = false
    }
  }

  private fun run(source: String) {
    val tokens = Scanner(source).scanTokens()
    tokens.forEach(::println)
  }

  fun reportError(line: Int, message: String) {
    System.err.println("[$line] Error: $message")
    error = true
  }
}
