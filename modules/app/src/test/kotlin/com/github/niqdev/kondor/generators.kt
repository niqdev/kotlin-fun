package com.github.niqdev.kondor

import com.ubertob.kondortools.generateConverterFileFor

fun main() {
  println(generateConverterFileFor(Person::class))
}
