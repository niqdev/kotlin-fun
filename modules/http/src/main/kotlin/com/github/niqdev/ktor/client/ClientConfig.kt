package com.github.niqdev.ktor.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.ConfigResult

data class ClientConfig(
  val baseUrl: String
) {
  companion object {
    private val objectMapper = jacksonObjectMapper()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true)

    fun load(): ConfigResult<ClientConfig> =
      ConfigLoader().loadConfig<ClientConfig>("/client.conf")
  }

  override fun toString(): String =
    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
}
