package com.github.niqdev

sealed class RedBlackTree<out T : Comparable<@UnsafeVariance T>> {

  abstract val color: Color

  internal object Empty : RedBlackTree<Nothing>() {
    // the empty tree is black
    override val color: Color = Color.Black
    override fun toString(): String = "Empty"
  }

  internal class Leaf<T : Comparable<T>>(
    override val color: Color,
    val left: MyTree<T>,
    val value: T,
    val right: MyTree<T>
  ) : RedBlackTree<T>() {
    override fun toString(): String = "Leaf($color, $left, $value, $right)"
  }

  companion object {
    operator fun <T : Comparable<T>> invoke(): RedBlackTree<T> = Empty
  }

  sealed class Color {
    internal object Red : Color() {
      override fun toString(): String = "Red"
    }

    internal object Black : Color() {
      override fun toString(): String = "Black"
    }
  }
}

fun <T : Comparable<T>> RedBlackTree<T>.size(): Int =
  when (this) {
    is RedBlackTree.Empty -> 0
    is RedBlackTree.Leaf -> 1 + left.size() + right.size()
  }

fun <T : Comparable<T>> RedBlackTree<T>.height(): Int =
  when (this) {
    is RedBlackTree.Empty -> -1
    is RedBlackTree.Leaf -> 1 + kotlin.math.max(left.height(), right.height())
  }

// a tree is nonempty and red
fun <T : Comparable<T>> RedBlackTree<T>.isTreeRed(): Boolean =
  when (this) {
    is RedBlackTree.Empty -> false
    is RedBlackTree.Leaf -> this.color == RedBlackTree.Color.Red
  }

// a tree is nonempty and black
fun <T : Comparable<T>> RedBlackTree<T>.isTreeBlack(): Boolean =
  when (this) {
    is RedBlackTree.Empty -> false
    is RedBlackTree.Leaf -> this.color == RedBlackTree.Color.Black
  }
