package com.github.niqdev.ktor.server

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class ServerConfig(
  val name: String,
  val database: DatabaseConfig,
) {
  companion object {
    private val objectMapper =
      jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

    fun load(values: Map<String, Any?>): Result<ServerConfig> = runCatching { objectMapper.convertValue(values, ServerConfig::class.java) }
  }

  override fun toString(): String = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}

data class DatabaseConfig(
  val url: String,
  val username: String,
  val password: String,
)
