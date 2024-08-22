package com.github.niqdev.kondor

import com.ubertob.kondor.json.JAny
import com.ubertob.kondor.json.jsonnode.JsonNodeObject
import com.ubertob.kondor.json.str

// https://github.com/uberto/kondor-json
// https://pragprog.com/titles/uboop/from-objects-to-functions
// https://github.com/uberto/swapi

// generate request
// https://swapi.dev
// convert json to data class
// https://toolbox.http4k.org/dataclass

data class Person(
  val name: String,
)

// generated with
// modules/app/src/test/kotlin/com/github/niqdev/kondor/generators.kt
object JPerson : JAny<Person>() {
  private val name by str(Person::name)

  override fun JsonNodeObject.deserializeOrThrow(): Person =
    Person(
      name = +name,
    )
}
