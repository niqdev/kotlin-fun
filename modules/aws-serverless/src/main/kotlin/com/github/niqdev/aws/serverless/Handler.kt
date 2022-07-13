package com.github.niqdev.aws.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestHandler

class Handler : RequestHandler<String, String> {
  override fun handleRequest(input: String?, context: Context?): String {
    val logger: LambdaLogger? = context?.logger
    logger?.log("INPUT: $input")
    return "TODO"
  }
}

/*
https://stackoverflow.com/questions/62814212/cant-use-httpapi-while-deploying-lambda-functions-to-localstack-using-serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent

class Handler : RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  override fun handleRequest(input: APIGatewayProxyRequestEvent?, context: Context?): APIGatewayProxyResponseEvent {
    val logger: LambdaLogger? = context?.logger
    logger?.log("INPUT: $input")
    // echo
    return APIGatewayProxyResponseEvent().apply {
      body = input?.body
      statusCode = 200
    }
  }
}

 */
