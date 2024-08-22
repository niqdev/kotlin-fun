package com.github.niqdev.ktor.server

import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test

class ApplicationTest {
  @Test
  fun `verify runtime config loading`() {
    testApplication {
      application {
        loadConfigOrThrow()
      }
    }
  }
}
