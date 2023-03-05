package com.github.niqdev.example

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class FreeBSpec : WordSpec({

  "FreeB" should {

    "example" {
      val valid = FreeB.And(
        FreeB.Pure(Predicate.AtLeast13(15)),
        FreeB.Pure(Predicate.NonEmptyName("martin"))
      )
      valid.run(Predicate.eval) shouldBe true
      valid.pretty() shouldBe "(at-least-13=15 && non-empty=martin)"

      FreeB.And(
        FreeB.Pure(Predicate.AtLeast13(5)),
        FreeB.Pure(Predicate.NonEmptyName("martin"))
      ).run(Predicate.eval) shouldBe false

      FreeB.And(
        FreeB.Pure(Predicate.AtLeast13(13)),
        FreeB.Pure(Predicate.NonEmptyName(""))
      ).run(Predicate.eval) shouldBe false
    }
  }
})
