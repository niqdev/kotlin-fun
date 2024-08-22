package com.github.niqdev.example

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class FreeBSpec :
  WordSpec({

    "FreeB" should {

      "example" {
        val valid =
          FreeB.And(
            FreeB.Pure(Predicate.AtLeast13(18)),
            FreeB.Pure(Predicate.NonEmptyName("martin")),
          )
        valid.run(Predicate.eval) shouldBe true
        valid.pretty() shouldBe "(at-least-13=18 && non-empty=martin)"

        FreeB
          .And(
            FreeB.Pure(Predicate.AtLeast13(5)),
            FreeB.Pure(Predicate.NonEmptyName("martin")),
          ).run(Predicate.eval) shouldBe false

        FreeB
          .And(
            FreeB.Pure(Predicate.AtLeast13(18)),
            FreeB.Pure(Predicate.NonEmptyName("")),
          ).run(Predicate.eval) shouldBe false
      }

      "eval many" {
        data class Person(
          val name: String,
          val age: Int,
        )

        fun isAllowed(person: Person): FreeB<Predicate> =
          pure(Predicate.AtLeast13(person.age)) and pure(Predicate.NonEmptyName(person.name))

        isAllowed(Person("martin", 18)).run(Predicate.eval) shouldBe true

        val people =
          listOf(
            Person("martin", 18),
            Person("alice", 12),
            Person("bob", 15),
          )

        people.fold(true) { allAllowed, p ->
          allAllowed && isAllowed(p).run(Predicate.eval)
        } shouldBe false
      }
    }
  })
