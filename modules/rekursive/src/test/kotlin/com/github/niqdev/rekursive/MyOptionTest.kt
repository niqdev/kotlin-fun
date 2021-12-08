package com.github.niqdev.rekursive

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class MyOptionTest : WordSpec({

  "MyOption" should {

    "verify show" {
      MyOption<Int>().show() shouldBe "None"
      MyOption(42).show() shouldBe "Some(42)"
    }

    "verify isEmpty" {
      MyOption<Int>().isEmpty() shouldBe true
      MyOption(42).isEmpty() shouldBe false
    }

    "verify Functor" {
      MyOptionFunctor.map<Int, String>(MyOption(42))() { "$it" } shouldBe MyOption("42")
    }
  }
})
