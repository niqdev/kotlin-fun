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
  when (ltree) {
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
    is MyTree.MyLeaf ->
      // while iterating, keep the tree without without the removed element
      when {
        element < value -> MyTree.MyLeaf(left.remove(element), value, right)
        element > value -> MyTree.MyLeaf(left, value, right.remove(element))
        // found
        else -> removeMerge(left, right, element)
      }
  }

// ---------- 10.7 ----------

// TODO verify: merge same root
fun <T : Comparable<T>> MyTree<T>.merge(tree: MyTree<T>): MyTree<T> =
  when (tree) {
    is MyTree.MyEmpty -> this
    is MyTree.MyLeaf ->
      when (this) {
        is MyTree.MyEmpty -> tree
        is MyTree.MyLeaf ->
          when {
            tree.value < this.value ->
              MyTree.MyLeaf(left, value, right.merge(MyTree.MyLeaf(MyTree.MyEmpty, tree.value, tree.right))).merge(tree.left)
            tree.value > this.value ->
              MyTree.MyLeaf(left.merge(MyTree.MyLeaf(tree.left, tree.value, MyTree.MyEmpty)), value, right).merge(tree.right)
            else ->
              MyTree.MyLeaf(left.merge(tree.left), value, right.merge(tree.right))
          }
      }
  }

// ---------- 10.8 ----------

// TODO verify: bi-recursive
// multiple implementations
// Post-order left
// Pre-order left
// Post-order right
// Pre-order right
// In-order left
// In-order right
fun <A : Comparable<A>, B> MyTree<A>.foldLeft(): (B) -> ((B) -> (A) -> B) -> ((B) -> (B) -> B) -> B =
  { identity ->
    { f ->
      { g ->
        when (this) {
          is MyTree.MyEmpty -> identity
          is MyTree.MyLeaf ->
            g(right.foldLeft<A, B>()(identity)(f)(g))(f(left.foldLeft<A, B>()(identity)(f)(g))(this.value))
        }
      }
    }
  }

// ---------- 10.9 ----------

fun <A : Comparable<A>, B> MyTree<A>.foldInOrder(): (B) -> ((B) -> (A) -> (B) -> B) -> B =
  { identity ->
    { f ->
      when (this) {
        is MyTree.MyEmpty -> identity
        is MyTree.MyLeaf -> f(left.foldInOrder<A, B>()(identity)(f))(value)(right.foldInOrder<A, B>()(identity)(f))
      }
    }
  }
fun <A : Comparable<A>, B> MyTree<A>.foldPreOrder(): (B) -> ((A) -> (B) -> (B) -> B) -> B =
  { identity ->
    { f ->
      when (this) {
        is MyTree.MyEmpty -> identity
        is MyTree.MyLeaf -> f(value)(left.foldPreOrder<A, B>()(identity)(f))(right.foldPreOrder<A, B>()(identity)(f))
      }
    }
  }
fun <A : Comparable<A>, B> MyTree<A>.foldPostOrder(): (B) -> ((B) -> (B) -> (A) -> B) -> B =
  { identity ->
    { f ->
      when (this) {
        is MyTree.MyEmpty -> identity
        is MyTree.MyLeaf -> f(left.foldPostOrder<A, B>()(identity)(f))(right.foldPostOrder<A, B>()(identity)(f))(value)
      }
    }
  }

// ---------- 10.10 ----------

fun <A : Comparable<A>> MyTree<A>.invoke(): (MyTree<A>) -> (A) -> (MyTree<A>) -> MyTree<A> =
  { left -> { a -> { right -> TODO() } } }

// ---------- 10.11 ----------

fun <A : Comparable<A>, B : Comparable<B>> MyTree<A>.map(): ((A) -> B) -> MyTree<B> =
  { f -> foldInOrder<A, MyTree<B>>()(MyTree.MyEmpty)() { b1 -> { a -> { b2 -> MyTree.MyLeaf(b1, f(a), b2) } } } }

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
  println(MyTree.from(MyList(1, 2, 3)).merge(MyTree.from(MyList(4, 5, 6))))
  println(exampleTree.foldLeft<Int, MyList<Int>>()(MyList())() { ints -> { a -> ints.cons()(a) } }() { x -> { y -> y.concat()(x) } })

  // In-order: 1234567
  // Pre-order: 4213657
  // Post-order: 1325764
  val anotherTree: MyTree<Int> =
    MyTree.MyLeaf(
      MyTree.MyLeaf(MyTree.leaf(1), 2, MyTree.leaf(3)),
      4,
      MyTree.MyLeaf(MyTree.leaf(5), 6, MyTree.leaf(7))
    )
  println(exampleTree.map<Int, Int>()() { it * 2 })
}
