package com.github.niqdev.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Project(
  val name: String,
  val language: String,
)

// https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md
fun main() {
  val dataValue = Project("kotlinx.serialization", "Kotlin")
  val dataString = """{"name":"kotlinx.serialization","language":"Kotlin"}"""
  println("${Json.encodeToString(dataValue)} | ${Json.decodeFromString<Project>(dataString)}")
}
