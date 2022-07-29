package com.github.niqdev.json

import com.github.niqdev.json.core.Either
import com.github.niqdev.json.core.NonEmptyList
import com.github.niqdev.json.core.ValidatedNel
import com.github.niqdev.json.core.toNel
import org.everit.json.schema.ValidationException
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import org.everit.json.schema.Schema as EveritSchema

sealed interface SchemaError
object InternalError : SchemaError
data class ValidationError(val message: String) : SchemaError

sealed interface Schema {
  fun validate(json: String): ValidatedNel<SchemaError, Unit>

  companion object {

    private fun loadUnsafe(value: String): EveritSchema =
      SchemaLoader.builder()
        .schemaJson(JSONTokener(value).nextValue())
        .draftV7Support()
        .build()
        .load()
        .build()

    // TODO uberto/kondor-json
    fun load(value: String): Either<Throwable, Schema> =
      Either.catch { loadUnsafe(value) }.map { EveritSchemaImpl(it) }
  }
}

private class EveritSchemaImpl(val schema: EveritSchema) : Schema {

  override fun validate(json: String): ValidatedNel<SchemaError, Unit> =
    Either
      .catch { schema.validate(JSONObject(json)) }
      .mapError { toNelErrors(it) }
      .toValidated()

  private fun toNelErrors(error: Throwable): NonEmptyList<SchemaError> =
    when (error) {
      is ValidationException -> {
        val errors = error.allMessages.map { ValidationError(it) }.toNel()
        when (errors) {
          is Either.Left -> NonEmptyList.of(InternalError)
          is Either.Right -> errors.value
        }
      }
      else -> NonEmptyList.of(InternalError)
    }
}
