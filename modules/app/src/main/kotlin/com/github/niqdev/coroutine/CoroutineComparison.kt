package com.github.niqdev.coroutine

import com.github.niqdev.coroutine.CoroutineComparison.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

  // "launch" blocks the scope and prevents to return
  suspend fun run(): String =
    coroutineScope {
      val fastResponse = async {
        fastRequest()
      }

      launch(Dispatchers.Default) {
        // call await multiple times to get the same result
        val result = compare(fastResponse.await(), slowRequest())
        logger.info("result=$result")
      }

      val response = fastResponse.await()
      logger.info("response=$response")
      response
    }
}

/*
 * returns as soon as runBlocking finishes and kills the background job if it takes longer without waiting
 *
 * see -Dkotlinx.coroutines.debug
 */
fun main() {
  // alternative fire and forget: "delicate api" risk of memory leaks
  // GlobalScope.launch {}

  // fire and forget i.e. daemon thread
  CoroutineScope(Dispatchers.IO).launch {
    logger.debug("before background")
    // change this delay to test the background job
    delay(10.toDuration(DurationUnit.SECONDS))
    logger.debug("after background")
  }

  runBlocking {
    logger.info("before blocking")
    val result = CoroutineComparison.run()
    logger.info("after blocking: $result")
  }
}

// TODO see withContext and context https://blog.jetbrains.com/kotlin/2022/02/kotlin-1-6-20-m1-released
// TODO see also asCompletableFuture().thenAcceptAsync{}.exceptionlly{}
