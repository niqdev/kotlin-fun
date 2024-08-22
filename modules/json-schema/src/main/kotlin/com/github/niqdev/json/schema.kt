package com.github.niqdev.json

import com.github.niqdev.json.core.Either
import com.github.niqdev.json.core.NonEmptyList
import com.github.niqdev.json.core.ValidatedNel
import com.github.niqdev.json.core.toNel
import io.confluent.kafka.schemaregistry.CompatibilityLevel
import io.confluent.kafka.schemaregistry.json.JsonSchema
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener

sealed interface SchemaError

data class InternalError(
  val message: String,
) : SchemaError

data class ValidationError(
  val message: String,
) : SchemaError

data class CompatibilityError(
  val message: String,
) : SchemaError

private class CompatibilityException(
  val errors: List<String>,
) : RuntimeException()

sealed interface Schema {
  fun validate(json: String): ValidatedNel<SchemaError, Unit>

  fun isBackwardCompatible(previousSchema: String): ValidatedNel<SchemaError, Unit>

  fun isForwardCompatible(previousSchema: String): ValidatedNel<SchemaError, Unit>

  companion object {
    private fun loadUnsafe(value: String): org.everit.json.schema.Schema =
      SchemaLoader
        .builder()
        .schemaJson(JSONTokener(value).nextValue())
        .draftV7Support()
        .build()
        .load()
        .build()

    // TODO uberto/kondor-json
    fun load(value: String): Either<Throwable, Schema> = Either.catch { loadUnsafe(value) }.map { InternalSchemaImpl(value, it) }
  }
}

private class InternalSchemaImpl(
  val rawSchema: String,
  val schema: org.everit.json.schema.Schema,
) : Schema {
  override fun validate(json: String): ValidatedNel<SchemaError, Unit> =
    Either
      .catch { schema.validate(JSONObject(json)) }
      .mapError { toSchemaErrorNel(it) }
      .toValidated()

  override fun isBackwardCompatible(previousSchema: String): ValidatedNel<SchemaError, Unit> =
    Either
      .catch { verifyCompatibilityUnsafe(CompatibilityLevel.BACKWARD, previousSchema) }
      .mapError { toSchemaErrorNel(it) }
      .toValidated()

  override fun isForwardCompatible(previousSchema: String): ValidatedNel<SchemaError, Unit> =
    Either
      .catch { verifyCompatibilityUnsafe(CompatibilityLevel.FORWARD, previousSchema) }
      .mapError { toSchemaErrorNel(it) }
      .toValidated()

  private fun verifyCompatibilityUnsafe(
    compatibilityLevel: CompatibilityLevel,
    previousSchema: String,
  ) {
    val errors = JsonSchema(rawSchema).isCompatible(compatibilityLevel, listOf(JsonSchema(previousSchema)))
    if (errors.isNotEmpty()) throw CompatibilityException(errors)
  }

  private fun toSchemaErrorNel(error: Throwable): NonEmptyList<SchemaError> =
    when (error) {
      is ValidationException ->
        when (val errors = error.allMessages.map { ValidationError(it) }.toNel()) {
          is Either.Left -> NonEmptyList.of(InternalError("unexpected validation error"))
          is Either.Right -> errors.value
        }
      is CompatibilityException ->
        when (val errors = error.errors.map { CompatibilityError(it) }.toNel()) {
          is Either.Left -> NonEmptyList.of(InternalError("unexpected compatibility error"))
          is Either.Right -> errors.value
        }
      else -> NonEmptyList.of(InternalError("unexpected schema error"))
    }
}
