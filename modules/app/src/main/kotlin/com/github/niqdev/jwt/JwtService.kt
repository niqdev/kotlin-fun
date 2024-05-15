package com.github.niqdev.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant
import java.util.UUID

interface JwtService {
  fun encode(payload: JwtPayload): String
  fun decode(token: String): Result<JwtPayload>
}

class JwtServiceImpl(
  private val config: JwtConfig
) : JwtService {

  private companion object {
    val mapper = jacksonObjectMapper()
      .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
      .registerModule(JavaTimeModule())
  }

  private val algorithm = Algorithm.HMAC256(config.secret)

  override fun encode(payload: JwtPayload): String {
    val timestamp = Instant.now()

    return JWT.create()
      .withIssuer(config.issuer) // identifies the party that created the token and signed it
      .withSubject("${config.issuer}-foo") // identifies the subject of the JWT
      .withIssuedAt(timestamp) // identifies the time at which the JWT was created
      .withExpiresAt(payload.expiresAt) // identifies the expiration time of the JWT
      .withJWTId(UUID.randomUUID().toString()) // unique identifier for the JWT
      .withNotBefore(timestamp) // identifies the time before the JWT should not be accepted for processing
      .withPayload(mapper.writeValueAsString(payload)) // alternative to "withClaim" builder
      .sign(algorithm)
  }

  // verifies online https://jwt.io
  override fun decode(token: String): Result<JwtPayload> = runCatching {
    val verifier = JWT.require(algorithm)
      .withIssuer(config.issuer)
      .build()

    val decodedJwt = verifier.verify(token)

    JwtPayload(
      userId = UUID.fromString(decodedJwt.getClaim("user_id").asString()),
      session = decodedJwt.getClaim("session").asInt(),
      expiresAt = decodedJwt.getClaim("expires_at").asInstant(),
    )
  }
}
