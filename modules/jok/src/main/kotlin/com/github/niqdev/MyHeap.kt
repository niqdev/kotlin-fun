package com.github.niqdev

// a heap-ordered leftist tree
sealed interface MyHeap<out T : Comparable<@UnsafeVariance T>> {

  object MyEmpty : MyHeap<Nothing>

  class MyHead<T : Comparable<T>>(
    val rank: Int,
    val left: MyHeap<T>,
    val head: T,
    val right: MyHeap<T>,
  ) : MyHeap<T>

  companion object {
    // to compile requires `out` and `@UnsafeVariance`
    operator fun <T : Comparable<T>> invoke(): MyHeap<T> =
      MyEmpty

    operator fun <T : Comparable<T>> invoke(element: T): MyHeap<T> =
      MyHead(1, MyEmpty, element, MyEmpty)
  }
}

fun <T : Comparable<T>> MyHeap<T>.pretty(): String =
  when (this) {
    is MyHeap.MyEmpty -> "Empty"
    is MyHeap.MyHead -> "Heap(${left.pretty()}, $head, ${right.pretty()})"
  }

fun <T : Comparable<T>> MyHeap<T>.isEmpty(): Boolean =
  when (this) {
    is MyHeap.MyEmpty -> true
    is MyHeap.MyHead -> false
  }

fun <T : Comparable<T>> MyHeap<T>.left(): Result<MyHeap<T>> =
  when (this) {
    is MyHeap.MyEmpty -> Result(MyHeap.MyEmpty)
    is MyHeap.MyHead -> Result(left)
  }

fun <T : Comparable<T>> MyHeap<T>.right(): Result<MyHeap<T>> =
  when (this) {
    is MyHeap.MyEmpty -> Result(MyHeap.MyEmpty)
    is MyHeap.MyHead -> Result(right)
  }

fun <T : Comparable<T>> MyHeap<T>.head(): Result<T> =
  when (this) {
    is MyHeap.MyEmpty -> Result.failure("head() called on empty heap")
    is MyHeap.MyHead -> Result(head)
  }

fun <T : Comparable<T>> MyHeap<T>.rank(): Int =
  when (this) {
    is MyHeap.MyEmpty -> 0
    is MyHeap.MyHead -> rank
  }

fun <T : Comparable<T>> MyHeap<T>.size(): Int =
  when (this) {
    is MyHeap.MyEmpty -> 0
    is MyHeap.MyHead -> left.size() + right.size() + 1
  }

// ---------- 11.5 ----------

private fun <T : Comparable<T>> mergeHead(head: T, first: MyHeap<T>, second: MyHeap<T>): MyHeap<T> =
  when {
    first.rank() >= second.rank() -> MyHeap.MyHead(second.rank() + 1, first, head, second)
    else -> MyHeap.MyHead(first.rank() + 1, second, head, first)
  }

private fun <T : Comparable<T>> merge(first: MyHeap<T>, second: MyHeap<T>): MyHeap<T> =
  first.head().flatMap<T, MyHeap<T>>()() { fh ->
    second.head().flatMap<T, MyHeap<T>>()() { sh ->
      when {
        fh <= sh ->
          first.left().flatMap<MyHeap<T>, MyHeap<T>>()() { fl ->
            first.right().map<MyHeap<T>, MyHeap<T>>()() { fr ->
              mergeHead(fh, fl, merge(fr, second))
            }
          }
        else ->
          second.left().flatMap<MyHeap<T>, MyHeap<T>>()() { sl ->
            second.right().map<MyHeap<T>, MyHeap<T>>()() { sr ->
              mergeHead(sh, sl, merge(first, sr))
            }
          }
      }
    }
  }.getOrElse()() {
    when (first) {
      is MyHeap.MyEmpty -> second
      is MyHeap.MyHead -> first
    }
  }

private fun <T : Comparable<T>> MyHeap<T>.add(): (T) -> MyHeap<T> =
  { element -> merge(this, MyHeap(element)) }

operator fun <T : Comparable<T>> MyHeap<T>.plus(element: T): MyHeap<T> =
  this.add()(element)

// ---------- 11.6 ----------

fun <T : Comparable<T>> MyHeap<T>.tail(): Result<MyHeap<T>> =
  when (this) {
    is MyHeap.MyEmpty -> Result.failure("tail() called on empty heap")
    is MyHeap.MyHead -> Result(merge(left, right))
  }

// ---------- 11.7 ----------

fun <T : Comparable<T>> MyHeap<T>.get(): (Int) -> Result<T> =
  { index ->
    when (this) {
      is MyHeap.MyEmpty -> Result.failure("Index out of bounds")
      is MyHeap.MyHead ->
        when (index) {
          0 -> Result(head)
          else -> tail().flatMap<MyHeap<T>, T>()() { it.get()(index - 1) }
        }
    }
  }

// ---------- 11.8 ----------

fun <T : Comparable<T>> MyHeap<T>.pop(): Option<Pair<T, MyHeap<T>>> =
  when (this) {
    is MyHeap.MyEmpty -> Option.None
    is MyHeap.MyHead -> tail().map<MyHeap<T>, Pair<T, MyHeap<T>>>()() { head to it }.toOption()
  }

fun <T : Comparable<T>> MyHeap<T>.toList(): MyList<T> =
  MyList<T>().unfold<T, MyHeap<T>>()(this)() { it.pop() }

// ------------------------------

fun main() {
  val heap = MyHeap<Int>() + 1 + 2 + 3 + 4 + 5
  println(heap.pretty())
  println((heap + 6).pretty())
  println((heap.tail().getOrElse()() { MyHeap.MyEmpty }).pretty())
  println(heap.get()(1))
  println(heap.get()(10))
  println(heap.toList())
}
