package com.github.niqdev.arrow

import arrow.core.left
import arrow.core.right
import com.github.niqdev.arrow.EitherExample.validateName
import com.github.niqdev.arrow.ExampleGenerators.exampleArb
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.forAll

object ExampleGenerators {
  val emailArb =
    Arb.string().flatMap { prefix ->
      Arb.string().map { suffix ->
        "$prefix@$suffix"
      }
    }

  val exampleArb =
    Arb.bind(
      Arb.string(),
      Arb.int(),
      emailArb,
    ) { name, age, email -> ExampleForm(name, age, email) }
}

class ValidationProp :
  StringSpec({

    "validate name" {
      forAll(exampleArb) { form ->
        when {
          form.name.isBlank() -> form.validateName() == FormError.BlankName.left()
          else -> form.validateName() == form.name.right()
        }
      }
    }

    // example only
    "String size" {
      forAll<String, String> { a, b ->
        (a + b).length == a.length + b.length
      }

      checkAll<String, String> { a, b ->
        a + b shouldHaveLength a.length + b.length
      }
    }
  })
