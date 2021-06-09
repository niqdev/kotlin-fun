package com.github.niqdev

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import org.slf4j.LoggerFactory

class Handler : RequestHandler<String, String> {
  private val logger = LoggerFactory.getLogger(Handler::class.java)

  override fun handleRequest(input: String?, context: Context?): String {
    logger.info(input)
    return "TODO"
  }
}

fun main() {
  Handler().handleRequest("TODO", null)
}
