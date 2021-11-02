package com.github.niqdev

import kotlin.math.pow

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

fun <T : Comparable<T>> RedBlackTree<T>.isEmpty(): Boolean =
  when (this) {
    is RedBlackTree.Empty -> true
    is RedBlackTree.Leaf -> false
  }

fun <T : Comparable<T>> RedBlackTree<T>.contains(element: T): Boolean =
  when (this) {
    is RedBlackTree.Empty -> false
    is RedBlackTree.Leaf ->
      when {
        element < this.value -> left.contains(element)
        else -> element <= this.value || right.contains(element)
      }
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

fun <T : Comparable<T>> RedBlackTree<T>.unsafeLeft(): RedBlackTree<T>? =
  when (this) {
    is RedBlackTree.Empty -> null
    is RedBlackTree.Leaf -> this.left
  }

fun <T : Comparable<T>> RedBlackTree<T>.unsafeRight(): RedBlackTree<T>? =
  when (this) {
    is RedBlackTree.Empty -> null
    is RedBlackTree.Leaf -> this.right
  }

fun <T : Comparable<T>> RedBlackTree<T>.unsafeValue(): T? =
  when (this) {
    is RedBlackTree.Empty -> null
    is RedBlackTree.Leaf -> this.value
  }

/*
 * output T R (T B a x b) y (T B c z d)
 *
 *          Red
 *         ┌───┐
 *         │ y │
 *   Black └┬─┬┘ Black
 *   ┌───┐  │ │  ┌───┐
 *   │ x │<─┘ └─>│ z │
 *   └┬─┬┘       └┬─┬┘
 *    │ │         │ │
 * a<─┘ └─>b   c<─┘ └─>d
 *
 * https://asciiflow.com
 */
private fun <T : Comparable<T>> RedBlackTree<T>.balance(
  color: RedBlackTree.Color,
  left: RedBlackTree<T>,
  value: T,
  right: RedBlackTree<T>
): RedBlackTree<T> =
  when (this) {
    is RedBlackTree.Empty -> this
    is RedBlackTree.Leaf ->
      // FIXME nested pattern matching to remove unsafe !!
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
        color == RedBlackTree.Color.Black && left.isTreeRed() && left.unsafeLeft()!!.isTreeRed() ->
          RedBlackTree.Leaf(
            RedBlackTree.Color.Red,
            left.unsafeLeft()!!.blacken(),
            left.unsafeValue()!!,
            RedBlackTree.Leaf(RedBlackTree.Color.Black, left.unsafeRight()!!, value, right)
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
        color == RedBlackTree.Color.Black && left.isTreeRed() && left.unsafeRight()!!.isTreeRed() ->
          RedBlackTree.Leaf(
            RedBlackTree.Color.Red,
            RedBlackTree.Leaf(
              RedBlackTree.Color.Black,
              left.unsafeLeft()!!,
              left.unsafeValue()!!,
              left.unsafeRight()!!.unsafeLeft()!!
            ),
            left.unsafeRight()!!.unsafeValue()!!,
            RedBlackTree.Leaf(RedBlackTree.Color.Black, left.unsafeRight()!!.unsafeRight()!!, value, right)
          )
        /*
         * balance B a x (T R (T R b y c) z d)  -->  T R (T B a x b) y (T B c z d)
         *
         *   Black
         *   ┌───┐
         *   │ x │
         *   └┬─┬┘  Red
         *    │ │  ┌───┐
         * a<─┘ └─>│ z │
         *    Red  └┬─┬┘
         *   ┌───┐  │ │
         *   │ y │<─┘ └─>d
         *   └┬─┬┘
         *    │ │
         * b<─┘ └─>c
         */
        color == RedBlackTree.Color.Black && right.isTreeRed() && right.unsafeLeft()!!.isTreeRed() ->
          RedBlackTree.Leaf(
            RedBlackTree.Color.Red,
            RedBlackTree.Leaf(
              RedBlackTree.Color.Black,
              left,
              value,
              right.unsafeLeft()!!.unsafeLeft()!!
            ),
            right.unsafeLeft()!!.unsafeValue()!!,
            RedBlackTree.Leaf(
              RedBlackTree.Color.Black,
              right.unsafeLeft()!!.unsafeRight()!!,
              right.unsafeValue()!!,
              right.unsafeRight()!!
            )
          )
        /*
         * balance B a x (T R b y (T R c z d))  -->  T R (T B a x b) y (T B c z d)
         *
         *   Black
         *   ┌───┐
         *   │ x │
         *   └┬─┬┘  Red
         *    │ │  ┌───┐
         * a<─┘ └─>│ y │
         *         └┬─┬┘  Red
         *          │ │  ┌───┐
         *       b<─┘ └─>│ z │
         *               └┬─┬┘
         *                │ │
         *             c<─┘ └─>d
         */
        color == RedBlackTree.Color.Black && right.isTreeRed() && right.unsafeRight()!!.isTreeRed() ->
          RedBlackTree.Leaf(
            RedBlackTree.Color.Red,
            RedBlackTree.Leaf(RedBlackTree.Color.Black, left, value, right.unsafeLeft()!!),
            right.unsafeValue()!!,
            right.unsafeRight()!!.blacken()
          )
        else ->
          // balance color a x b = T color a x b
          RedBlackTree.Leaf(color, left, value, right)
      }
  }

private fun <T : Comparable<T>> RedBlackTree<T>.blacken(): RedBlackTree<T> =
  when (this) {
    is RedBlackTree.Empty -> this
    is RedBlackTree.Leaf -> RedBlackTree.Leaf(RedBlackTree.Color.Black, left, value, right)
  }

private fun <T : Comparable<T>> RedBlackTree<T>.add(): (T) -> RedBlackTree<T> =
  { valueToAdd ->
    when (this) {
      is RedBlackTree.Empty ->
        RedBlackTree.Leaf(RedBlackTree.Color.Red, RedBlackTree.Empty, valueToAdd, RedBlackTree.Empty)
      is RedBlackTree.Leaf ->
        when {
          valueToAdd < value -> balance(color, left.add()(valueToAdd), value, right)
          valueToAdd > value -> balance(color, left, value, right.add()(valueToAdd))
          else -> RedBlackTree.Leaf(color, left, valueToAdd, right)
        }
    }
  }

operator fun <T : Comparable<T>> RedBlackTree<T>.plus(element: T): RedBlackTree<T> =
  this.add()(element).blacken()

// ------------------------------

private fun <T : Comparable<T>> RedBlackTree<T>.bubble(
  color: RedBlackTree.Color,
  left: RedBlackTree<T>,
  value: T,
  right: RedBlackTree<T>
): RedBlackTree<T> = TODO()

private fun <T : Comparable<T>> RedBlackTree<T>.remove(): RedBlackTree<T> = TODO()

// TODO https://github.com/pysaumont/fpinkotlin/blob/master/fpinkotlin-parent/fpinkotlin-advancedtrees-solutions/src/main/kotlin/com/fpinkotlin/advancedtrees/exercise02/Tree.kt
private fun <T : Comparable<T>> RedBlackTree<T>.delete(): (T) -> RedBlackTree<T> =
  { valueToDelete ->
    when (this) {
      is RedBlackTree.Empty -> this
      is RedBlackTree.Leaf ->
        when {
          valueToDelete < value -> bubble(color, left.delete()(valueToDelete), value, right)
          valueToDelete > value -> bubble(color, left, value, right.delete()(valueToDelete))
          else -> TODO() // remove()
        }
    }
  }

operator fun <T : Comparable<T>> RedBlackTree<T>.minus(element: T): RedBlackTree<T> =
  this.delete()(element).blacken()

// ------------------------------

fun <T : Comparable<T>> RedBlackTree<T>.pretty(): String {

  fun makeCell(string: String): String =
    when (string.length) {
      1 -> " $string  "
      2 -> " $string "
      3 -> "$string "
      else -> string
    }

  fun <A : Comparable<A>> makeTable(
    table: Array<Array<String>>,
    tree: RedBlackTree<A>,
    hPosition: Int,
    vPosition: Int
  ): Array<Array<String>> =
    when (tree) {
      is RedBlackTree.Empty -> table
      is RedBlackTree.Leaf -> {
        val shift = 2.0.pow((tree.height() - 1).toDouble()).toInt()
        val lhPosition = hPosition - shift
        val rhPosition = hPosition + shift
        table[vPosition][hPosition] = "${tree.color}${tree.value}"
        val t2 = makeTable(table, tree.left, lhPosition, vPosition - 1)
        makeTable(t2, tree.right, rhPosition, vPosition - 1)
      }
    }

  val tableHeight = this.height() + 1
  val tableWidth = 2.0.pow((this.height() + 1).toDouble()).toInt() - 1
  val table: Array<Array<String>> = Array(tableHeight) { Array(tableWidth) { "    " } }
  val hPosition = tableWidth / 2
  val vPosition = this.height()
  val result = makeTable(table, this, hPosition, vPosition)
  val sb = StringBuilder()
  for (l in result.size downTo 1) {
    for (c in 0 until result[0].size) {
      sb.append(makeCell(result[l - 1][c]))
    }
    sb.append("\n")
  }
  return sb.toString()
}

// ------------------------------

fun main() {
  println((RedBlackTree.Empty + 3 + 2 + 1).pretty())
  println((RedBlackTree.Empty + 3 + 1 + 2).pretty())
  println((RedBlackTree.Empty + 1 + 3 + 2).pretty())
  println((RedBlackTree.Empty + 1 + 2 + 3).pretty())
  val tenAsc = RedBlackTree.Empty + 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9 + 10 + 11
  println(tenAsc.pretty())
  val tenDesc = RedBlackTree.Empty + 11 + 10 + 9 + 8 + 7 + 6 + 5 + 4 + 3 + 2 + 1
  println(tenDesc.pretty())

  val tree = RedBlackTree.Empty + 1 + 4 + 3 + 2 + 5
  println(tree)
  println(tree.size())
  println(tree.height())
  println(tree.isEmpty())
  println(tree.contains(3))
  println(tree.contains(6))
}
