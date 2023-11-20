package com.github.niqdev.ktor.server

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

data class ServerConfig(
  val name: String
) {
  companion object {
    private val objectMapper = jacksonObjectMapper()

    fun load(values: Map<String, Any?>): Result<ServerConfig> =
      runCatching { objectMapper.convertValue(values, ServerConfig::class.java) }
  }

  override fun toString(): String =
    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}
