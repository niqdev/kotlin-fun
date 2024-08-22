package com.github.niqdev.hoplite

import com.sksamuel.hoplite.ConfigLoader

data class Example(
  val myKey: Int,
  val myValue: String,
)

fun main() {
  val config = ConfigLoader().loadConfig<Example>("/example.yaml")
  println(config.getUnsafe())
}
