package com.github.niqdev

// a heap-ordered leftist tree: used to implement priority queues
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
    operator fun <T : Comparable<T>> invoke(): MyHeap<T> = MyEmpty

    operator fun <T : Comparable<T>> invoke(element: T): MyHeap<T> = MyHead(1, MyEmpty, element, MyEmpty)
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

fun <T : Comparable<T>> MyHeap<T>.left(): MyResult<MyHeap<T>> =
  when (this) {
    is MyHeap.MyEmpty -> MyResult(MyHeap.MyEmpty)
    is MyHeap.MyHead -> MyResult(left)
  }

fun <T : Comparable<T>> MyHeap<T>.right(): MyResult<MyHeap<T>> =
  when (this) {
    is MyHeap.MyEmpty -> MyResult(MyHeap.MyEmpty)
    is MyHeap.MyHead -> MyResult(right)
  }

fun <T : Comparable<T>> MyHeap<T>.head(): MyResult<T> =
  when (this) {
    is MyHeap.MyEmpty -> MyResult.failure("head() called on empty heap")
    is MyHeap.MyHead -> MyResult(head)
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

private fun <T : Comparable<T>> mergeHead(
  head: T,
  first: MyHeap<T>,
  second: MyHeap<T>,
): MyHeap<T> =
  when {
    first.rank() >= second.rank() -> MyHeap.MyHead(second.rank() + 1, first, head, second)
    else -> MyHeap.MyHead(first.rank() + 1, second, head, first)
  }

private fun <T : Comparable<T>> merge(
  first: MyHeap<T>,
  second: MyHeap<T>,
): MyHeap<T> =
  first
    .head()
    .flatMap<T, MyHeap<T>> { fh ->
      second.head().flatMap<T, MyHeap<T>> { sh ->
        when {
          fh <= sh ->
            first.left().flatMap<MyHeap<T>, MyHeap<T>> { fl ->
              first.right().map<MyHeap<T>, MyHeap<T>> { fr ->
                mergeHead(fh, fl, merge(fr, second))
              }
            }
          else ->
            second.left().flatMap<MyHeap<T>, MyHeap<T>> { sl ->
              second.right().map<MyHeap<T>, MyHeap<T>> { sr ->
                mergeHead(sh, sl, merge(first, sr))
              }
            }
        }
      }
    }.getOrElse {
      when (first) {
        is MyHeap.MyEmpty -> second
        is MyHeap.MyHead -> first
      }
    }

private fun <T : Comparable<T>> MyHeap<T>.add(): (T) -> MyHeap<T> = { element -> merge(this, MyHeap(element)) }

operator fun <T : Comparable<T>> MyHeap<T>.plus(element: T): MyHeap<T> = this.add()(element)

// ---------- 11.6 ----------

fun <T : Comparable<T>> MyHeap<T>.tail(): MyResult<MyHeap<T>> =
  when (this) {
    is MyHeap.MyEmpty -> MyResult.failure("tail() called on empty heap")
    is MyHeap.MyHead -> MyResult(merge(left, right))
  }

// ---------- 11.7 ----------

fun <T : Comparable<T>> MyHeap<T>.get(): (Int) -> MyResult<T> =
  { index ->
    when (this) {
      is MyHeap.MyEmpty -> MyResult.failure("Index out of bounds")
      is MyHeap.MyHead ->
        when (index) {
          0 -> MyResult(head)
          else -> tail().flatMap<MyHeap<T>, T> { it.get()(index - 1) }
        }
    }
  }

// ---------- 11.8 ----------

fun <T : Comparable<T>> MyHeap<T>.pop(): Option<Pair<T, MyHeap<T>>> =
  when (this) {
    is MyHeap.MyEmpty -> Option.None
    is MyHeap.MyHead -> tail().map<MyHeap<T>, Pair<T, MyHeap<T>>> { head to it }.toOption()
  }

fun <T : Comparable<T>> MyHeap<T>.toList(): MyList<T> = MyList<T>().unfold<T, MyHeap<T>>()(this) { it.pop() }

// ---------- 11.9 ----------

fun <A : Comparable<A>, B, Z> MyHeap<A>.unfold(
  identity: B,
  f: (B) -> (A) -> B,
): (Z) -> ((Z) -> Option<Pair<A, Z>>) -> B =
  { zero ->
    { getNext ->
      tailrec fun loop(
        z: Z,
        result: B,
      ): B =
        when (val next = getNext(z)) {
          is Option.None -> result
          is Option.Some ->
            loop(next.value.second, f(result)(next.value.first))
        }
      loop(zero, identity)
    }
  }

fun <A : Comparable<A>, B> MyHeap<A>.foldLeft(): (B) -> ((B) -> (A) -> B) -> B =
  { identity -> { f -> this.unfold<A, B, MyHeap<A>>(identity, f)(this) { it.pop() } } }

fun <T : Comparable<T>> MyHeap<T>.toListWithUnfold(): MyList<T> = this.foldLeft<T, MyList<T>>()(MyList.MyNil)(MyList<T>::cons).reverse()

// ---------- 11.10 ----------

// TODO
sealed interface MyHeapNotComparable<out T>

// ------------------------------

fun main() {
  val heap = MyHeap<Int>() + 1 + 2 + 3 + 4 + 5
  println(heap.pretty())
  println((heap + 6).pretty())
  println((heap.tail().getOrElse { MyHeap.MyEmpty }).pretty())
  println(heap.get()(1))
  println(heap.get()(10))
  println(heap.toList())
  println(heap.toListWithUnfold())
}
