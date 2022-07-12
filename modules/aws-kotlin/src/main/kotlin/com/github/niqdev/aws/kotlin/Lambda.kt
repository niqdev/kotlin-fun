package com.github.niqdev.aws.kotlin

import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.InvokeRequest
import org.slf4j.LoggerFactory

suspend fun main(args: Array<String>) {
  Lambda.invokeFunction("fn-hello")
}

object Lambda {
  private val logger = LoggerFactory.getLogger(Lambda::class.java)
  suspend fun invokeFunction(functionNameVal: String) {
    val json = """{"Hello":"World"}"""
    val byteArray = json.trimIndent().encodeToByteArray()
    val request = InvokeRequest {
      functionName = functionNameVal
      payload = byteArray
    }

    LambdaClient { region = "us-east-1" }.use { awsLambda ->
      val response = awsLambda.invoke(request)
      logger.info("response: $response")
    }
  }
}
