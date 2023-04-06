package com.github.niqdev.coroutine

import com.github.niqdev.coroutine.CoroutineComparison.logger
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object CoroutineComparison {
  val logger: Logger = LoggerFactory.getLogger(CoroutineComparison.javaClass)

  private suspend fun newRequest(seconds: Int, label: String): String {
    logger.debug("before: $label")
    delay(seconds.toDuration(DurationUnit.SECONDS))
    logger.debug("after $label")
    return "response-$label"
  }

  private suspend fun fastRequest(): String =
    newRequest(1, "fast")

  private suspend fun slowRequest(): String =
    newRequest(5, "slow")

  private fun compare(a: String, b: String): Boolean =
    a.split("-").first() == b.split("-").first()

  suspend fun run(): String =
    coroutineScope {
      val fastResponse = async {
        fastRequest()
      }

      val result = fastResponse.await()
      logger.info("fastResult=$result")

      launch(Dispatchers.Default) {
        // call await multiple times to get the result
        compare(fastResponse.await(), slowRequest())
      }

      // TODO return as soon as it finish and run comparison in background
      result
    }
}

// see -Dkotlinx.coroutines.debug
fun main() {
  runBlocking {
    logger.info("started")
    val result = CoroutineComparison.run()
    logger.info("finished=$result")
  }
}
