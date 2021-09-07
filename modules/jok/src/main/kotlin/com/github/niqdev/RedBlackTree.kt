package com.github.niqdev

/*
 * invariants to verify:
 * - an empty tree is black
 * - the left and right subtrees of a red tree are black: it's not possible to find two successive reds when descending the tree
 * - every path from the root to an empty subtree has the same number of blacks
 */
sealed class RedBlackTree<out T : Comparable<@UnsafeVariance T>> {

  abstract val color: Color

  internal object Empty : RedBlackTree<Nothing>() {
    // the empty tree is black
    override val color: Color = Color.Black
    override fun toString(): String = "Empty"
  }

  internal class Leaf<T : Comparable<T>>(
    override val color: Color,
    val left: RedBlackTree<T>,
    val value: T,
    val right: RedBlackTree<T>
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

// ---------- 11.1 ----------

/*
// balance B (T R (T R a x b) y c) z d = T R (T B a x b) y (T B c z d)
    color == B && left.isTR && left.left.isTR ->
        T(R, left.left.blacken(), left.value, T(B, left.right, value, right))
// balance B (T R a x (T R b y c)) z d = T R (T B a x b) y (T B c z d)
    color == B && left.isTR && left.right.isTR ->
        T(R, T(B, left.left, left.value, left.right.left), left.right.value,
          T(B, left.right.right, value, right))
// balance B a x (T R (T R b y c) z d) = T R (T B a x b) y (T B c z d)
    color == B && right.isTR && right.left.isTR ->
        T(R, T(B, left, value, right.left.left), right.left.value,
          T(B, right.left.right, right.value, right.right))
// balance B a x (T R b y (T R c z d)) = T R (T B a x b) y (T B c z d)
    color == B && right.isTR && right.right.isTR ->
        T(R, T(B, left, value, right.left), right.value, right.right.
     blacken())
// balance color a x b = T color a x b
    else -> T(color, left, value, right)
 */

// ---> https://asciiflow.com

private fun <T : Comparable<T>> RedBlackTree<T>.balance(
  color: RedBlackTree.Color,
  left: RedBlackTree<T>,
  value: T,
  right: RedBlackTree<T>
): RedBlackTree<T> =
  when (this) {
    is RedBlackTree.Empty -> this
    is RedBlackTree.Leaf ->
      when (this.left) {
        is RedBlackTree.Empty -> this
        is RedBlackTree.Leaf ->
          when {
            /*
             * balance B (T R (T R a x b) y c) z d  -->  T R (T B a x b) y (T B c z d)
             *
             *               Black
             *               ┌───┐
             *               │ z │
             *          Red  └┬─┬┘
             *         ┌───┐  │ │
             *         │ y │<─┘ └─>d
             *    Red  └┬─┬┘
             *   ┌───┐  │ │
             *   │ x │<─┘ └─>c
             *   └┬─┬┘
             *    │ │
             * a<─┘ └─>b
             */
            color == RedBlackTree.Color.Black && this.left.isTreeRed() && this.left.left.isTreeRed() ->
              RedBlackTree.Leaf(
                RedBlackTree.Color.Red,
                this.left.left.blacken(),
                this.left.value,
                RedBlackTree.Leaf(RedBlackTree.Color.Black, this.left.right, value, right)
              )
            /*
             * balance B (T R a x (T R b y c)) z d  -->  T R (T B a x b) y (T B c z d)
             *
             *         Black
             *         ┌───┐
             *         │ z │
             *    Red  └┬─┬┘
             *   ┌───┐  │ │
             *   │ x │<─┘ └─>d
             *   └┬─┬┘  Red
             *    │ │  ┌───┐
             * a<─┘ └─>│ y │
             *         └┬─┬┘
             *          │ │
             *       b<─┘ └─>c
             */
            color == RedBlackTree.Color.Black && this.left.isTreeRed() && this.left.right.isTreeRed() -> TODO()

            // balance B a x (T R (T R b y c) z d) = T R (T B a x b) y (T B c z d)
            color == RedBlackTree.Color.Black && this.right.isTreeRed() && this.right.left.isTreeRed() -> TODO()

            else ->
              // balance color a x b = T color a x b
              RedBlackTree.Leaf(color, left, value, right)
          }
      }
  }


private fun <T : Comparable<T>> RedBlackTree<T>.blacken(): RedBlackTree<T> = TODO()

private fun <T : Comparable<T>> RedBlackTree<T>.add(): RedBlackTree<T> = TODO()

operator fun <T : Comparable<T>> RedBlackTree<T>.plus(element: @UnsafeVariance T): RedBlackTree<T> = TODO()
