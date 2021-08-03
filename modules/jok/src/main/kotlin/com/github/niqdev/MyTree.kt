package com.github.niqdev

sealed class MyTree<out T> {

  abstract fun isEmpty(): Boolean

  internal object MyEmpty: MyTree<Nothing>() {
    override fun isEmpty(): Boolean = true
    override fun toString(): String = "Empty"
  }

  internal class MyLeaf<T>(
    val left: MyTree<T>,
    val value: T,
    val right: MyTree<T>
    ): MyTree<T>() {
    override fun isEmpty(): Boolean = false
    override fun toString(): String = "Leaf($left, value, $right)"
  }

  companion object {
    // requires MyTree covariant on T i.e. "out"
    operator fun <T> invoke(): MyTree<T> = MyEmpty
  }
}

// ------------------------------
