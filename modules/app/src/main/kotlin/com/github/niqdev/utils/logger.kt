package com.github.niqdev.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC

fun <R : Any> R.logger(): Lazy<Logger> {
  return lazy { LoggerFactory.getLogger(this::class.java.name.replace("\$Companion", "")) }
}

class ExampleLogger {
  companion object {
    private val log by logger()
  }

  fun run(): Unit =
    log.info("hello world")
}

fun main() {
  // Mapped Diagnostic Context (MDC)
  MDC.put("my-key", "my-value")

  ExampleLogger().run()
}
