// length, max, ... can be implemented using a `fold`, but such implementations have a major drawback: the time needed to compute the result is proportional to the length of the list e.g. what if size is 1 million
// memoization can turn a function that works in O(n) time (time proportional to the number of elements) into O(1) time (constant time)
// it has a time cost because it makes the insertion of elements slightly longer, but slowing insertion is generally not a big problem
// a much more important problem is the increase in memory space: in a mutable list, nothing prevent you from memoizing the list length as a mutable integer, which takes only 32 bits. But with an immutable list, you need to memoize the length in each element

sealed class MyListMemoized<out A> {

  abstract fun isEmpty(): Boolean

  internal object MyNil : MyListMemoized<Nothing>() {
    override fun isEmpty(): Boolean = true
    override fun toString(): String = "Nil"
  }

  internal class MyCons<A>(val head: A, val tail: MyListMemoized<A>, val size: Long) : MyListMemoized<A>() {
    override fun isEmpty(): Boolean = false
    override fun toString(): String {
      tailrec fun loop(tmp: MyListMemoized<A>, result: String): String =
        when (tmp) {
          is MyNil -> "${result}Nil"
          is MyCons -> loop(tmp.tail, "$result${tmp.head}, ")
        }
      return loop(this, "")
    }
  }

  companion object {
    operator fun <A> invoke(vararg items: A): MyListMemoized<A> {
      fun loop(result: List<A>, count: Long): MyListMemoized<A> =
        when {
          result.isEmpty() -> MyNil
          else -> MyCons(result[0], loop(result.drop(1), count + 1), count + 1)
        }
      return loop(items.asList(), 0L)
    }
  }
}

fun <A> MyListMemoized<A>.length(): Long =
  when (this) {
    is MyListMemoized.MyNil -> 0
    is MyListMemoized.MyCons -> this.size + 1 // TODO
  }
