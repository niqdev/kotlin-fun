package com.github.niqdev.arrow

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.core.zip
import arrow.typeclasses.Semigroup
import com.github.niqdev.arrow.EitherExample.validate
import com.github.niqdev.arrow.EitherExample.validateAge
import com.github.niqdev.arrow.EitherExample.validateEmail
import com.github.niqdev.arrow.EitherExample.validateName
import com.github.niqdev.arrow.ValidateExample.validatedNel

// Algebraic Data Type or Sum Type
sealed class FormError(
  private val error: String,
) {
  object BlankName : FormError("blank name")

  data class InvalidAge(
    val value: Int,
  ) : FormError("invalid age: $value")

  data class InvalidEmail(
    val value: String,
  ) : FormError("invalid email: $value")

  override fun toString(): String = "FormError($error)"
}

data class ExampleForm(
  val name: String,
  val age: Int,
  val email: String,
)

object EitherExample {
  fun ExampleForm.validateName(): Either<FormError, String> =
    if (this.name.isBlank()) {
      FormError.BlankName.left()
    } else {
      this.name.right()
    }

  fun ExampleForm.validateAge(): Either<FormError, Int> =
    if (this.age < 18) {
      FormError.InvalidAge(this.age).left()
    } else {
      age.right()
    }

  fun ExampleForm.validateEmail(): Either<FormError, String> =
    if (this.email.isBlank() || !this.email.contains('@')) {
      FormError.InvalidEmail(this.email).left()
    } else {
      this.email.right()
    }

  fun ExampleForm.validate(): Either<FormError, ExampleForm> =
    validateName().zip(this.validateAge(), this.validateEmail()) { name, age, email -> ExampleForm(name, age, email) }
}

object ValidateExample {
  fun ExampleForm.validatedNel() =
    validateName().toValidatedNel().zip(
      Semigroup.nonEmptyList(),
      validateAge().toValidatedNel(),
      validateEmail().toValidatedNel(),
    ) { name, age, email ->
      ExampleForm(name, age, email)
    }
}

fun main() {
  println(ExampleForm("", 10, "").validate())
  println(ExampleForm("name", 18, "my@email").validate())

  println(ExampleForm("", 10, "").validatedNel())
  println(ExampleForm("name", 18, "my@email").validatedNel())
}
