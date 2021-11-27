package com.github.niqdev.rekursion

// https://www.47deg.com/blog/recursion-schemes-introduction
// https://github.com/arrow-kt/arrow-incubator/blob/main/arrow-recursion-data/src/main/kotlin/arrow/recursion/data/Fix.kt

private fun <E, B> foldRightStep1(): (MyList<E>) -> (B) -> ((E, B) -> B) -> B =
  { init ->
    { z ->
      { op ->
        when (init) {
          is MyList.Nil -> z
          is MyList.Cons -> op(init.head, foldRightStep1<E, B>()(init.tail)(z)(op))
        }
      }
    }
  }

private fun <E, A> unfoldStep1(): (A) -> ((A) -> MyOption<Pair<E, A>>) -> MyList<E> =
  { init ->
    { f ->
      when (val pair = f(init)) {
        is MyOption.None -> MyList.Nil
        is MyOption.Some -> MyList.Cons(pair.value.first, unfoldStep1<E, A>()(pair.value.second)(f))
      }
    }
  }

// ------------------------------

private fun <E, B> foldRightStep2(): (MyList<E>) -> ((MyOption<Pair<E, B>>) -> B) -> B =
  { init ->
    { f ->
      when (init) {
        is MyList.Nil -> f(MyOption.None)
        is MyList.Cons -> f(MyOption(init.head to foldRightStep2<E, B>()(init.tail)(f)))
      }
    }
  }

// ------------------------------

private fun <E, B> foldRightStep3(): ((MyOption<Pair<E, B>>) -> B) -> (MyList<E>) -> B =
  { f ->
    { init ->
      // lazy val ???
      fun kernel(): (MyList<E>) -> B = { list ->
        when (list) {
          is MyList.Nil -> f(MyOption.None)
          is MyList.Cons -> f(MyOption(list.head to kernel()(list.tail)))
        }
      }
      kernel()(init)
    }
  }

private fun <E, A> unfoldStep3(): ((A) -> MyOption<Pair<E, A>>) -> (A) -> MyList<E> =
  { f ->
    { init ->
      // lazy val ???
      fun kernel(): (A) -> MyList<E> = { a ->
        when (val pair = f(a)) {
          is MyOption.None -> MyList.Nil
          is MyOption.Some -> MyList.Cons(pair.value.first, kernel()(pair.value.second))
        }
      }
      kernel()(init)
    }
  }

// ------------------------------

private fun <E, B> foldRightStep4(): ((MyOption<Pair<E, B>>) -> B) -> (MyList<E>) -> B =
  { f ->
    { init ->
      fun unpack(): (MyList<E>) -> MyOption<Pair<E, MyList<E>>> = TODO()
      fun recurse(): (MyOption<Pair<E, MyList<E>>>) -> MyOption<Pair<E, B>> = TODO()

      // this is `f`
      fun compute(): (MyOption<Pair<E, B>>) -> B = TODO()

      compute()(recurse()(unpack()(init)))
    }
  }

// ------------------------------

private fun <E, B> foldRightStep5(): ((MyOption<Pair<E, B>>) -> B) -> (MyList<E>) -> B =
  { f ->
    { init ->
      // type aliases are top-level only
      // typealias F<P> = MyOption<Pair<E, P>>
      // typealias S = MyList<E>

      fun kernel(): (MyList<E>) -> B = { list ->
        // S => F[S]
        fun unpack(): (MyList<E>) -> MyOption<Pair<E, MyList<E>>> =
          { xs ->
            when (xs) {
              is MyList.Nil -> MyOption.None
              is MyList.Cons -> MyOption.Some(xs.head to xs.tail)
            }
          }

        // F[S] => F[B]
        fun recurse(): (MyOption<Pair<E, MyList<E>>>) -> MyOption<Pair<E, B>> =
          { option ->
            when (option) {
              is MyOption.None -> MyOption.None
              is MyOption.Some -> MyOption(option.value.first to kernel()(option.value.second))
            }
          }

        f(recurse()(unpack()(list)))
      }
      kernel()(init)
    }
  }

// ------------------------------

interface Kind<out F, out A>

interface Functor<F> {
  // def map[A, B](fa: F[A])(f: A => B): F[B]
  fun <A, B> map(fa: Kind<F, A>): ((A) -> B) -> Kind<F, B>
}

private fun <E, B> foldRightStep6(): ((MyOption<Pair<E, B>>) -> B) -> (MyList<E>) -> B =
  { f ->
    { init ->

      // val optionFunctor: Functor<MyOption<>> =

      fun kernel(): (MyList<E>) -> B = { list ->
        // S => F[S]
        fun unpack(): (MyList<E>) -> MyOption<Pair<E, MyList<E>>> =
          { xs ->
            when (xs) {
              is MyList.Nil -> MyOption.None
              is MyList.Cons -> MyOption.Some(xs.head to xs.tail)
            }
          }

        // F[S] => F[B]
        fun recurse(): (MyOption<Pair<E, MyList<E>>>) -> MyOption<Pair<E, B>> = TODO()
        // FIXME { option -> MyOptionFunctor.map<Pair<E, MyList<E>>, Pair<E, B>>(option)(kernel()).fix() }

        f(recurse()(unpack()(list)))
      }
      kernel()(init)
    }
  }

// type ListF[A, B] = Option[(A, B)]
typealias MyListF<A, B> = MyOption<Pair<A, B>>

private fun <E> projectList(): (MyList<E>) -> MyListF<E, MyList<E>> =
  { list ->
    when (list) {
      is MyList.Nil -> MyOption.None
      is MyList.Cons -> MyOption(list.head to list.tail)
    }
  }

private fun <F, S, B> foldRightStep7(
  f: (Kind<F, B>) -> B,
  project: (S) -> Kind<F, S>,
  ff: Functor<F>
): (S) -> B =
  { s ->
    fun kernel(init: S): B = f(ff.map<S, B>(project(init))(::kernel))
    kernel(s)
  }

// ------------------------------

object Steps {

  fun step1() {
    val prodOp: (Int, Int) -> Int = { a, b -> a * b }
    // 200
    println(foldRightStep1<Int, Int>()(MyList(1, 10, 20))(1)(prodOp))

    val rangeOp: (Int) -> MyOption<Pair<Int, Int>> =
      { v ->
        if (v <= 0) MyOption.None
        else MyOption.Some(v to v - 1)
      }
    // List(10, 9, 8, 7, 6, 5, 4, 3, 2, 1, Nil)
    println(unfoldStep1<Int, Int>()(10)(rangeOp))
  }

  fun step2() {
    val prodF: (MyOption<Pair<Int, Int>>) -> Int =
      { option ->
        when (option) {
          is MyOption.None -> 1
          is MyOption.Some -> option.value.first * option.value.second
        }
      }
    println(foldRightStep2<Int, Int>()(MyList(1, 10, 20))(prodF))
  }

  fun step7() {
    val prodListF: (MyListF<Int, Int>) -> Int =
      { option ->
        when (option) {
          is MyOption.None -> 1
          is MyOption.Some -> option.value.first * option.value.second
        }
      }
    // FIXME
    // println(foldRightStep7(prodListF, projectList<Int>(), MyOptionFunctor)(MyList(1, 10, 20)))
  }
}

fun main() {
  Steps.step1()
  Steps.step2()
}
