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
    println(">>> ${System.getProperties()}")
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
  fun runPrompt(): Unit {
    val input = InputStreamReader(System.`in`)
    val reader = BufferedReader(input)

    while (true) {
      print("> ")
      reader.readLine()
      error = false
    }
  }

  fun run(source: String) = println(source)
  /*
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // For now, just print the tokens.
    for (Token token : tokens) {
      System.out.println(token);
    }
  }
   */

  fun reportError(line: Int, message: String) {
    System.err.println("[$line] Error: $message")
    error = true
  }
}
