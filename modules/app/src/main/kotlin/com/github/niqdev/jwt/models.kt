package com.github.niqdev.jwt

import java.time.Instant
import java.util.UUID

data class JwtConfig(
  val secret: String,
  val issuer: String,
)

data class JwtPayload(
  val userId: UUID,
  val session: Int,
  val expiresAt: Instant,
)
