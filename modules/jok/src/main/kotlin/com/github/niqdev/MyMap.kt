package com.github.niqdev

class MyMapEntry<K : Comparable<K>, V> private constructor(
  private val key: K,
  private val value: MyResult<V>
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
      MyMapEntry(pair.first, MyResult(pair.second))

    operator fun <K : Comparable<K>, V> invoke(key: K): MyMapEntry<K, V> =
      MyMapEntry(key, MyResult.Empty)

    fun <K : Comparable<K>, V> of(key: K, value: V): MyMapEntry<K, V> =
      MyMapEntry(key, MyResult(value))
  }
}

// binary search trees are used for maps, also called dictionaries or associative arrays
class MyMap<K : Comparable<K>, V> private constructor(
  // TODO use RedBlackTree instead
  private val tree: MyTree<MyMapEntry<K, V>> = MyTree.MyEmpty

  // ---------- 11.4 ----------

  // how to handle collisions with NON-Comparable values: see issue in 11.3
  // private val tree0: RedBlackTree<MyMapEntry<K, MyList<Pair<K, V>>>> = RedBlackTree.Empty
) {

  operator fun plus(entry: Pair<K, V>): MyMap<K, V> =
    MyMap(this.tree + MyMapEntry(entry))

  operator fun minus(key: K): MyMap<K, V> =
    MyMap(this.tree - MyMapEntry(key))

  operator fun get(key: K): MyResult<MyMapEntry<K, V>> =
    tree[MyMapEntry(key)]

  fun contains(): (K) -> Boolean =
    { key -> tree.contains(MyMapEntry(key)) }

  fun isEmpty(): Boolean =
    tree.isEmpty()

  fun size(): Int =
    tree.size()

  // ---------- 11.3 ----------

  // TODO compile issue due to Comparable V ???
  fun values(): MyList<V> = TODO()
  // tree.foldInReverseOrder<Result<V>, MyList<Result<V>>>()(MyList())() { tmpResult -> { item -> { result -> result.concat()(tmpResult.cons()(item)) } } }

  override fun toString() =
    tree.toString()

  companion object {
    operator fun <K : Comparable<K>, V> invoke(): MyMap<K, V> = MyMap()
  }
}
