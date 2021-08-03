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

fun <T : Comparable<T>> MyTree<T>.size(): Int = TODO()
fun <T : Comparable<T>> MyTree<T>.height(): Int = TODO()

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
}
