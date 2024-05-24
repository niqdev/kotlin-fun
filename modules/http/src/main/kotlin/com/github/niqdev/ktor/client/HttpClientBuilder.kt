package com.github.niqdev.ktor.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.jackson.jackson

object HttpClientBuilder {

  fun build(builder: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit = {}): HttpClient =
    HttpClient(Java) {
      install(Logging)
      install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 5)
        exponentialDelay()
      }
    }

  fun buildJson(builder: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit = {}): HttpClient =
    build {
      install(ContentNegotiation) {
        jackson {
          configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)
          propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
          registerModule(JavaTimeModule())
          disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
      }
    }
}
