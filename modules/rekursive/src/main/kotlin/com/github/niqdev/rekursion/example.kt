package com.github.niqdev.rekursion

// Generalization of `foldRight` to `Catamorphism` and `unfold` to `Anamorphism`
// https://www.47deg.com/blog/recursion-schemes-introduction
// https://nrinaudo.github.io/recschemes

// ------------------------------

// definitions: `foldRight` is the dual of `unfold`

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

// think of `init`, which gives us a value of type `B`, as a function from `Unit` to `B`
// init ----------> B
// () ---- init ----> B

// `f` also return a value of `B`
// (E, B) -> B

// we could combine them together, taking a `Unit` or a `Pair<E, B>`

// we could use `Either` (no right biased)
// Either<Unit, Pair<E, B>> ----f----> B

// we could use `Option` and encode Unit as None
// Option<Pair<E, B>> ----f----> B

// later replace MyOptionOf<Pair<A, B>> with MyListF<A, B> and encode Unit as Nil

private fun <E, B> foldRightStep2(): (MyList<E>) -> ((MyOption<Pair<E, B>>) -> B) -> B =
  { init ->
    { f ->
      when (init) {
        is MyList.Nil -> f(MyOption.None)
        // in next step
        // * try to avoid passing initial values and f at every step
        // * remove curried parameter for better readability
        is MyList.Cons -> f(MyOption(init.head to foldRightStep2<E, B>()(init.tail)(f)))
      }
    }
  }

// ------------------------------

// return a function instead of a value
// introduce nested function
// no more recursive on foldRight and unfold

private fun <E, B> foldRightStep3a(): ((MyOption<Pair<E, B>>) -> B) -> (MyList<E>) -> B =
  { f ->
    // lazy val ???
    fun kernel(): (MyList<E>) -> B = { list ->
      when (list) {
        is MyList.Nil -> f(MyOption.None)
        is MyList.Cons -> f(MyOption(list.head to kernel()(list.tail)))
      }
    }
    kernel()
  }

private fun <E, B> foldRightStep3b(): ((MyOption<Pair<E, B>>) -> B) -> (MyList<E>) -> B =
  { f ->
    fun kernel(list: MyList<E>): B =
      when (list) {
        is MyList.Nil -> f(MyOption.None)
        is MyList.Cons -> f(MyOption(list.head to kernel(list.tail)))
      }
    ::kernel
  }

private fun <E, B> foldRightStep3c(f: (MyOption<Pair<E, B>>) -> B): (MyList<E>) -> B =
  {
    fun kernel(list: MyList<E>): B =
      when (list) {
        is MyList.Nil -> f(MyOption.None)
        is MyList.Cons -> f(MyOption(list.head to kernel(list.tail)))
      }
    kernel(it)
  }

private fun <E, A> unfoldStep3(): ((A) -> MyOption<Pair<E, A>>) -> (A) -> MyList<E> =
  { f ->
    fun kernel(): (A) -> MyList<E> = { a ->
      when (val pair = f(a)) {
        is MyOption.None -> MyList.Nil
        is MyOption.Some -> MyList.Cons(pair.value.first, kernel()(pair.value.second))
      }
    }
    kernel()
  }

// ------------------------------

// factor out the data structure: introduce functions one for each step

private fun <E, B> foldRightStep4(): ((MyOption<Pair<E, B>>) -> B) -> (MyList<E>) -> B =
  { f ->
    { init ->
      // pattern matching on the list: unpacking or projecting a data structure
      fun unpack(): (MyList<E>) -> MyOption<Pair<E, MyList<E>>> = TODO()
      // recursive call in the case the list is not empty with `kernel`
      fun recurse(): (MyOption<Pair<E, MyList<E>>>) -> MyOption<Pair<E, B>> = TODO()
      // computing the result using `f`
      fun compute(): (MyOption<Pair<E, B>>) -> B = TODO()

      compute()(recurse()(unpack()(init)))
    }
  }

// ------------------------------

private fun <E, B> foldRightStep5(): ((MyOption<Pair<E, B>>) -> B) -> (MyList<E>) -> B =
  { f ->
    // type aliases can be top-level only
    // typealias F<P> = MyOption<Pair<E, P>>
    // typealias S = MyList<E>

    // (S) -> B
    fun kernel(): (MyList<E>) -> B = { init ->
      // (S) -> F<S>
      fun unpack(): (MyList<E>) -> MyOption<Pair<E, MyList<E>>> = {
        when (it) {
          is MyList.Nil -> MyOption.None
          is MyList.Cons -> MyOption.Some(it.head to it.tail)
        }
      }
      // this implementation can be replaced with a Functor
      // (F<S>) -> F<B>
      fun recurse(): (MyOption<Pair<E, MyList<E>>>) -> MyOption<Pair<E, B>> = {
        when (it) {
          is MyOption.None -> MyOption.None
          is MyOption.Some -> MyOption(it.value.first to kernel()(it.value.second))
        }
      }
      // f: (F<P>) -> B
      f(recurse()(unpack()(init)))
    }

    kernel()
  }

// ------------------------------

private fun <F, S, B> foldRightStep6(
  f: (Kind<F, B>) -> B,
  project: (S) -> Kind<F, S>,
  ff: Functor<F>
): (S) -> B =
  { s ->
    fun kernel(init: S): B = f(ff.map<S, B>(project(init))(::kernel))
    kernel(s)
  }

// ------------------------------

/**
 * Also called F-Algebra
 */
typealias Algebra<F, A> = (Kind<F, A>) -> A
typealias CoAlgebra<F, A> = (A) -> Kind<F, A>

// TODO instance of Functor: map is an extension without implementation
private fun <F, A, B> cata(
  algebra: (Kind<F, A>) -> A,
  project: (B) -> Kind<F, B>
): (B) -> A {
  fun loop(init: B): A = algebra(project(init).map(::loop))
  return ::loop
}

// ------------------------------

// FIXME (continue)
// https://nrinaudo.github.io/recschemes/fix.html

/*

private fun <F, A> cataFix1(
  algebra: (Kind<F, A>) -> A,
  project: (Fix<F>) -> Kind<F, FixOf<A>>,
  ff: Functor<F>
): (Fix<F>) -> A {
  fun loop(state: Fix<F>): A = algebra(ff.map<A, Fix<F>>(project(state))(::loop))
  return ::loop
}

def cataFix[F[_]: Functor, A](
  algebra: F[A] => A,
  project: Fix[F] => F[Fix[F]]
): Fix[F] => A = {
  def loop(state: Fix[F]): A =
    algebra(map(project(state), loop))
  loop
}

*/

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

  fun step6() {
    fun <E> projectList(): (MyList<E>) -> ListF<E, MyList<E>> =
      { list ->
        when (list) {
          is MyList.Nil -> ListF.Nil
          is MyList.Cons -> ListF.Cons(list.head, list.tail)
        }
      }

    // ----------

    val intListFFunctor: ListFFunctor<Int> = object : ListFFunctor<Int> {}

    // these are also Monoids!

    val productOpA: Algebra<ListFPartialOf<Int>, Int> = {
      when (val state = it.fix()) {
        is ListF.Nil -> 1
        is ListF.Cons -> state.head * state.tail
      }
    }

    val sumOpA: Algebra<ListFPartialOf<Int>, Int> = {
      when (val state = it.fix()) {
        is ListF.Nil -> 0
        is ListF.Cons -> state.head + state.tail
      }
    }

    // cata
    println(foldRightStep6(productOpA, projectList(), intListFFunctor)(MyList(1, 10, 20)))
    println(foldRightStep6(sumOpA, projectList(), intListFFunctor)(MyList(1, 10, 20)))
  }
}

fun main() {
  Steps.step1()
  Steps.step2()
  Steps.step6()
}
