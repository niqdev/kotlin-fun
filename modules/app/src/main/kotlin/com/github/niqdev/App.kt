package com.github.niqdev

import org.slf4j.LoggerFactory

class App {
  val greeting: String
    get() {
      return "Hello World!"
    }
}

fun main() {
  val logger = LoggerFactory.getLogger(App::class.java)
  logger.info("main: ${App().greeting}")
}
