package com.github.niqdev.ktor.models

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

// https://kotlinlang.org/docs/inline-classes.html
@JvmInline
value class UserIdInlineJacksonIssue(private val uuid: java.util.UUID) {

  companion object {
    fun from(value: String): Result<UserId> =
      runCatching { java.util.UUID.fromString(value) }.map(::UserId)
  }

  val value: String
    get() = uuid.toString()
}

// TODO workaround to value class issue
// https://github.com/FasterXML/jackson-module-kotlin/issues/413
// https://github.com/FasterXML/jackson-module-kotlin/issues/199
@JsonSerialize(using = UserIdSerializer::class)
@JsonDeserialize(using = UserIdDeserializer::class)
data class UserId(val uuid: java.util.UUID) {
  companion object {

    fun fromStringUnsafe(value: String): UserId =
      UserId(java.util.UUID.fromString(value))

    fun fromString(value: String): Result<UserId> =
      runCatching { java.util.UUID.fromString(value) }.map(::UserId)
  }
}

internal class UserIdSerializer : StdSerializer<UserId>(UserId::class.java) {
  override fun serialize(value: UserId?, gen: JsonGenerator?, provider: SerializerProvider?) =
    gen?.writeString(value?.uuid.toString())
      ?: throw IllegalArgumentException("Unable to serialize UserId: $value")
}

internal class UserIdDeserializer : StdDeserializer<UserId>(UserId::class.java) {
  override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): UserId =
    p?.readValueAs(java.util.UUID::class.java)?.let { UserId(it) }
      ?: throw IllegalArgumentException("Unable to deserialize UserId")
}
