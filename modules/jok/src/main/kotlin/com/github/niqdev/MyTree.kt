package com.github.niqdev

// Comparable is contravariant on T
sealed class MyTree<out T : Comparable<@UnsafeVariance T>> {

  abstract fun isEmpty(): Boolean

  internal object MyEmpty : MyTree<Nothing>() {
    override fun isEmpty(): Boolean = true
    override fun toString(): String = "Empty"
  }

  internal class MyLeaf<T : Comparable<T>>(
    val left: MyTree<T>,
    val value: T,
    val right: MyTree<T>
  ) : MyTree<T>() {
    override fun isEmpty(): Boolean = false
    override fun toString(): String = "Leaf($left, $value, $right)"
  }

  companion object {
    // requires MyTree covariant on T i.e. "out"
    operator fun <T : Comparable<T>> invoke(): MyTree<T> = MyEmpty

    // ---------- 10.2 ----------

    fun <T : Comparable<T>> from(list: MyList<T>): MyTree<T> {
      fun loop(tmp: MyList<T>, result: MyTree<T>): MyTree<T> =
        when (tmp) {
          is MyList.MyNil -> result
          is MyList.MyCons -> loop(tmp.tail, result + tmp.head)
        }
      return loop(list, MyEmpty)
    }

    fun <T : Comparable<T>> leaf(value: T): MyTree<T> =
      MyLeaf(MyEmpty, value, MyEmpty)
  }
}

// ---------- 10.1 ----------

operator fun <T : Comparable<T>> MyTree<T>.plus(element: @UnsafeVariance T): MyTree<T> =
  when (this) {
    is MyTree.MyEmpty -> MyTree.MyLeaf(MyTree.MyEmpty, element, MyTree.MyEmpty)
    is MyTree.MyLeaf ->
      when {
        element < this.value -> MyTree.MyLeaf(left + element, this.value, this.right)
        element > this.value -> MyTree.MyLeaf(this.left, this.value, right + element)
        else -> MyTree.MyLeaf(left, element, this.right)
      }
  }

// ---------- 10.3 ----------

fun <T : Comparable<T>> MyTree<T>.contains(element: T): Boolean =
  when (this) {
    is MyTree.MyEmpty -> false
    is MyTree.MyLeaf ->
      when {
        element < value -> left.contains(element)
        element > value -> right.contains(element)
        else -> value == element
      }
  }

fun <T : Comparable<T>> MyTree<T>.containsFull(element: T): Boolean =
  when (this) {
    is MyTree.MyEmpty -> false
    is MyTree.MyLeaf -> value == element || left.containsFull(element) || right.containsFull(element)
  }

// ---------- 10.4 ----------

fun <T : Comparable<T>> MyTree<T>.size(): Int =
  when (this) {
    is MyTree.MyEmpty -> 0
    is MyTree.MyLeaf -> 1 + left.size() + right.size()
  }

fun <T : Comparable<T>> MyTree<T>.height(): Int =
  when (this) {
    // if it were 0, the height would be equal to the number of elements in the path instead of the number of segments
    is MyTree.MyEmpty -> -1
    is MyTree.MyLeaf -> 1 + kotlin.math.max(left.height(), right.height())
  }

// ---------- 10.5 ----------

fun <T : Comparable<T>> MyTree<T>.max(): Result<T> =
  when (this) {
    is MyTree.MyEmpty -> Result.Empty
    is MyTree.MyLeaf -> right.max().orElse()() { Result(value) }
  }

fun <T : Comparable<T>> MyTree<T>.min(): Result<T> =
  when (this) {
    is MyTree.MyEmpty -> Result.Empty
    is MyTree.MyLeaf -> left.max().orElse()() { Result(value) }
  }

// ---------- 10.6 ----------

private fun <T : Comparable<T>> removeMerge(ltree: MyTree<T>, rtree: MyTree<T>, element: T): MyTree<T> =
  // "this" is the root
  when (ltree) {
    // merge
    is MyTree.MyEmpty -> rtree
    is MyTree.MyLeaf ->
      when (rtree) {
        is MyTree.MyEmpty -> ltree
        is MyTree.MyLeaf ->
          // TODO fix merge
          when {
            // in the left branch replace with the right tree
            ltree.value < element -> MyTree.MyLeaf(ltree, rtree.value, rtree.right)
            // in the right branch replace with the left tree
            rtree.value > element -> MyTree.MyLeaf(ltree.left, ltree.value, rtree)
            else -> throw IllegalArgumentException("impossible")
          }
      }
  }

fun <T : Comparable<T>> MyTree<T>.remove(element: T): MyTree<T> =
  when (this) {
    is MyTree.MyEmpty -> this
    is MyTree.MyLeaf -> {
      // while iterating, keep the tree without without the removed element
      when {
        element < value -> MyTree.MyLeaf(left.remove(element), value, right)
        element > value -> MyTree.MyLeaf(left, value, right.remove(element))
        // found
        else -> removeMerge(left, right, element)
      }
    }
  }

fun main() {
  val myTree: MyTree<Int> =
    MyTree.MyLeaf(
      MyTree.MyLeaf(MyTree.MyEmpty, 5, MyTree.MyEmpty),
      0,
      MyTree.MyLeaf(MyTree.MyEmpty, 10, MyTree.MyEmpty)
    )
  println(myTree)
  println(myTree + 3 + 15 + 7)
  println(MyTree.from(MyList(1, 2, 3, 4, 5)))
  println(MyTree.from(MyList(1, 2, 3, 4, 5)).contains(3))
  println(MyTree.from(MyList(1, 2, 3, 4, 5)).containsFull(10))
  println(myTree.size())
  println(myTree.height())
  println(MyTree.from(MyList(1, 2, 3, 4, 5)).max())
  println(MyTree.from(MyList(1, 2, 3, 4, 5)).min())
  println(MyTree.from(MyList(1, 2, 3, 4, 5)).remove(4))

  val exampleTree: MyTree<Int> =
    MyTree.MyLeaf(
      MyTree.MyLeaf(MyTree.leaf(0), 1, MyTree.leaf(2)),
      3,
      MyTree.MyLeaf(
        MyTree.MyLeaf(MyTree.leaf(5), 6, MyTree.leaf(7)),
        8,
        MyTree.MyLeaf(MyTree.leaf(9), 10, MyTree.leaf(11))
      )
    )
  println(exampleTree)
  println(exampleTree.remove(3))
  println(exampleTree.remove(8))
}
