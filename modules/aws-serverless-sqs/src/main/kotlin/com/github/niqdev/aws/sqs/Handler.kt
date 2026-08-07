package com.github.niqdev.aws.sqs

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent

class Handler : RequestHandler<SQSEvent, String> {
  override fun handleRequest(input: SQSEvent, context: Context): String {
    context.logger.log("TODO")
    return "TODO"
  }
}
