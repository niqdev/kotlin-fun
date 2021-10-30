package com.github.niqdev

class MyMapEntry<K : Comparable<K>, V> private constructor(
  private val key: K,
  private val value: Result<V>
) : Comparable<MyMapEntry<K, V>> {

  override fun compareTo(other: MyMapEntry<K, V>): Int =
    key.compareTo(other.key)

  override fun toString(): String =
    "MapEntry($key, $value)"

  override fun equals(other: Any?): Boolean =
    this === other || when (other) {
      is MyMapEntry<*, *> -> key == other.key
      else -> false
    }

  override fun hashCode(): Int =
    key.hashCode()

  companion object {

    operator fun <K : Comparable<K>, V> invoke(pair: Pair<K, V>): MyMapEntry<K, V> =
      MyMapEntry(pair.first, Result(pair.second))

    operator fun <K : Comparable<K>, V> invoke(key: K): MyMapEntry<K, V> =
      MyMapEntry(key, Result.Empty)

    fun <K : Comparable<K>, V> of(key: K, value: V): MyMapEntry<K, V> =
      MyMapEntry(key, Result(value))
  }
}

// binary search trees are used for maps, also called dictionaries or associative arrays
class MyMap<K : Comparable<K>, V> private constructor(
  private val tree: RedBlackTree<MyMapEntry<K, V>> = RedBlackTree.Empty
) {

  operator fun plus(entry: Pair<K, V>): MyMap<K, V> =
    MyMap(this.tree + MyMapEntry(entry))

  operator fun minus(key: K): MyMap<K, V> = TODO()
  // MyMap(this.tree - MyMapEntry(key))

  operator fun get(key: K): Result<MyMapEntry<K, V>> =
    tree[MyMapEntry(key)]

  fun contains(): (K) -> Boolean =
    { key -> tree.contains(MyMapEntry(key)) }

  fun isEmpty(): Boolean =
    tree.isEmpty()

  fun size(): Int =
    tree.size()

  override fun toString() =
    tree.toString()

  companion object {
    operator fun <K : Comparable<K>, V> invoke(): MyMap<K, V> = MyMap()
  }
}
