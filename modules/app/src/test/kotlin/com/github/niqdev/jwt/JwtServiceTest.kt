package com.github.niqdev.jwt

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.UUID

class JwtServiceTest :
  WordSpec({

    "JwtService" should {
      "verify encode and decode" {
        val payload =
          JwtPayload(
            userId = UUID.fromString("a9a3eb19-a440-497f-b430-3ebd4fff3edc"),
            session = 8,
            // https://www.timestamp-converter.com
            expiresAt = Instant.ofEpochMilli(4070943000000), // 2099-01-01T09:30:00.000Z
          )
        val jwtService =
          JwtServiceImpl(
            config =
              JwtConfig(
                secret = "my-secret",
                issuer = "my-issuer",
              ),
          )
        jwtService.decode(jwtService.encode(payload)).getOrNull() shouldBe payload
      }
    }
  })
