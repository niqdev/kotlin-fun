package com.github.niqdev.clikt

import com.github.ajalt.clikt.core.CliktCommand

/**
 * https://ajalt.github.io/clikt
 *
 * ./gradlew runCliktExample -Pargs="--help"
 */
class CliktExample : CliktCommand() {
  override fun run() {
    echo("Hello World!")
  }
}

fun main(args: Array<String>) = CliktExample().main(args)
