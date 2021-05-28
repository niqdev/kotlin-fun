package com.github.niqdev.clikt

import com.github.ajalt.clikt.core.CliktCommand

/**
 * https://ajalt.github.io/clikt
 *
 * Default arguments
 * ./gradlew runCliktExample --args="--help"
 *
 * Custom property (see gradle task)
 * ./gradlew runCliktExample -PmyArgs="--help"
 */
class CliktExample : CliktCommand() {
  override fun run() {
    echo("Hello World!")
  }
}

fun main(args: Array<String>) = CliktExample().main(args)
