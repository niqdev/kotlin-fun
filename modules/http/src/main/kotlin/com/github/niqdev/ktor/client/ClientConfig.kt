package com.github.niqdev.ktor.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ConfigResult
import com.sksamuel.hoplite.addResourceSource

data class ClientConfig(
  val baseUrl: String
) {
  companion object {
    private val objectMapper = jacksonObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

    fun load(): ConfigResult<ClientConfig> =
      ConfigLoaderBuilder.default()
        .addResourceSource("/client.conf")
        .strict()
        .build()
        .loadConfig<ClientConfig>()
  }

  override fun toString(): String =
    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}
