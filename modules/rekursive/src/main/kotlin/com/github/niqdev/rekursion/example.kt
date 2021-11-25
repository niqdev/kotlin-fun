package com.github.niqdev.rekursion

// https://www.47deg.com/blog/recursion-schemes-introduction

fun <E, B> foldRight(): (MyList<E>) -> (B) -> ((E, B) -> B) -> B =
  { init ->
    { z ->
      { op ->
        when (init) {
          is MyList.Nil -> z
          is MyList.Cons -> op(init.head, foldRight<E, B>()(init.tail)(z)(op))
        }
      }
    }
  }

fun <E, A> unfold(): (A) -> ((A) -> MyOption<Pair<E, A>>) -> MyList<E> =
  { init ->
    { f ->
      when (val pair = f(init)) {
        is MyOption.None -> MyList.Nil
        is MyOption.Some -> MyList.Cons(pair.value.first, unfold<E, A>()(pair.value.second)(f))
      }
    }
  }

fun main() {
  val prodOp: (Int, Int) -> Int = { a, b -> a * b }
  // 200
  println(foldRight<Int, Int>()(MyList(1, 10, 20))(1)(prodOp))

  val rangeOp: (Int) -> MyOption<Pair<Int, Int>> =
    { v ->
      if (v <= 0) MyOption.None
      else MyOption.Some(v to v - 1)
    }
  // List(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, Nil)
  println(unfold<Int, Int>()(10)(rangeOp))
}
