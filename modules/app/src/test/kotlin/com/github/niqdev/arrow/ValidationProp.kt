package com.github.niqdev.arrow

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.property.checkAll
import io.kotest.property.forAll

class ValidationProp : StringSpec({

  "String size" {
    forAll<String, String> { a, b ->
      (a + b).length == a.length + b.length
    }

    checkAll<String, String> { a, b ->
      a + b shouldHaveLength a.length + b.length
    }
  }
})
