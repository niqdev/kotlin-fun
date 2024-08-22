package com.github.niqdev.ktor.server.arbitraries

import com.github.niqdev.ktor.models.User
import com.github.niqdev.ktor.models.UserId
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid

// TODO not used
val userArb =
  arbitrary {
    val id = Arb.uuid().map { UserId(it) }.bind()
    val name = Arb.string(0, 100).bind()
    val age = Arb.int(18..99).bind()
    User(id, name, age)
  }
