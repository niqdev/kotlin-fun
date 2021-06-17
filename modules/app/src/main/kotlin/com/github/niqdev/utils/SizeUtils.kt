package com.github.niqdev.utils

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

// https://stackoverflow.com/questions/13533485/is-there-any-way-to-get-the-size-in-bytes-of-a-string-in-java
private fun String.sizeInBytes() =
  this.toByteArray(Charsets.UTF_8).size

private fun String.humanReadableSize() =
  readableSize(this.sizeInBytes().toLong())

// https://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
private fun readableSize(size: Long): String =
  if (size <= 0) "0" else {
    val units = listOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = log10(size.toDouble()) / log10(1024.0)
    DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups)) + " " + units[digitGroups.toInt()]
  }

fun main() {
  val jsonString = """"{"key":"value","array":[1,2,3]}""""
  println(jsonString)
  println(jsonString.sizeInBytes())
  println(jsonString.humanReadableSize())
}
