package com.github.niqdev.utils

import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID

typealias ModelId = UUID

data class MyModel(
  val name: String,
  val value: Int
) {
  companion object {
    fun toId(name: String): ModelId =
      UuidCreator.getNameBasedSha1("my-$name")
  }
}
