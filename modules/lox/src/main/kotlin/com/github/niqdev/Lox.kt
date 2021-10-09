package com.github.niqdev

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

// >>> (1)

object Lox {

  private var error = false
  private var runtimeError = false

  @JvmStatic
  fun main(args: Array<String>) {
    println("Current working directory: ${System.getProperty("user.dir")}")

    when (args.size) {
      1 -> {
        println(">>> ${args[0]}")
        run(readFile(args[0]))
      }
      0 -> {
        // println("runPrompt")
        run(readFile("modules/lox/data/example4.lox"))
      }
      // https://www.freebsd.org/cgi/man.cgi?query=sysexits
      else -> {
        val errorCode =
          when {
            error -> 65
            runtimeError -> 70
            else -> -1
          }
        exitProcess(errorCode)
      }
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
    val statements = Parser(tokens).parse()

    if (error) return

    // TODO move outsite
    Interpreter().interpret(statements)
  }

  fun error(token: Token, message: String) =
    reportError(token.line, "[${token.lexeme}] $message")

  fun reportError(line: Int, message: String) {
    System.err.println("[$line] Error: $message")
    error = true
  }

  fun reportRuntimeError(error: LoxRuntimeError) {
    System.err.println("[${error.token.line}] RuntimeError: ${error.message}")
    runtimeError = true
  }
}
