package com.github.niqdev.aws.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import org.slf4j.LoggerFactory

class Handler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  companion object {
    private val log = LoggerFactory.getLogger(Handler::class.java)
  }

  override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
    // example with internal logger
    val logger: LambdaLogger? = context?.logger
    logger?.log("INPUT from context: $input")
    log.info("INPUT from log4j2: $input")

    // echo
    return APIGatewayProxyResponseEvent().apply {
      body = input?.body
      statusCode = 200
    }
  }
}
