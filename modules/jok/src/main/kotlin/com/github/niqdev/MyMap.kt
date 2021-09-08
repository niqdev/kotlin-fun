package com.github.niqdev

class MyMap<K : Comparable<K>, V> {

  override fun toString() = TODO()

  companion object {
    operator fun <K : Comparable<K>, V> invoke(): MyMap<K, V> = MyMap()
  }
}

operator fun <K : Comparable<K>, V> MyMap<K, V>.plus(entry: Pair<K, V>): MyMap<K, V> = TODO()

operator fun <K : Comparable<K>, V> MyMap<K, V>.minus(key: K): MyMap<K, V> = TODO()

fun <K : Comparable<K>, V> MyMap<K, V>.contains(): (K) -> MyMap<K, V> = TODO()

// operator fun <K : Comparable<K>, V> MyMap<K, V>.get(key: K): Result<MapEntry<K, V>> = TODO()

fun <K : Comparable<K>, V> MyMap<K, V>.isEmpty(): Boolean = TODO()

fun <K : Comparable<K>, V> MyMap<K, V>.size(): Int = TODO()
