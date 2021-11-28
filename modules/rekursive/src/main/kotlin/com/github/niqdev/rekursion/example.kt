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

// typealias F<E, P> = MyOption<Pair<E, P>>
// typealias S<E> = MyList<E>

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

interface Functor<F> {
  // def map[A, B](fa: F[A])(f: A => B): F[B]
  // fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
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
// TODO MyOptionOf vs MyOption
typealias MyListF<A, B> = MyOptionOf<Pair<A, B>>

// ---

sealed class TodoForMyListF private constructor()

typealias TodoMyListFOf<F, A> = Kind2<TodoForMyListF, F, A>

typealias TodoMyListFPartialOf<F> = Kind<TodoForMyListF, F>

interface TodoMyListFFunctor<F> : Functor<TodoMyListFPartialOf<F>> {
  override fun <A, B> map(fa: Kind<TodoMyListFPartialOf<F>, A>): ((A) -> B) -> Kind<TodoMyListFPartialOf<F>, B> =
    { f ->
      when (val a = fa.fix()) {
        is TodoMyListF.TodoMyNil -> TodoMyListF.TodoMyNil
        is TodoMyListF.TodoMyCons -> TodoMyListF.TodoMyCons(a.head, f(a.tail))
      }
    }
  fun <A, B> Kind<TodoMyListFPartialOf<F>, A>.mapExt(f: (A) -> B): Kind<TodoMyListFPartialOf<F>, B> =
    map<A, B>(this)(f)
  companion object
}

@Suppress("NOTHING_TO_INLINE")
inline fun <F, A> TodoMyListFOf<F, A>.fix(): TodoMyListF<F, A> =
  this as TodoMyListF<F, A>

sealed interface TodoMyListF<out F, out A> : TodoMyListFOf<F, A> {
  data class TodoMyCons<out F, out A>(val head: F, val tail: A) : TodoMyListF<F, A>
  object TodoMyNil : TodoMyListF<Nothing, Nothing>
}

object todo {
  val intTodoMyListFFunctor: TodoMyListFFunctor<Int> = object : TodoMyListFFunctor<Int> {}
  fun aaa() {
    // intTodoMyListFFunctor.map<>()
  }
}

/*



sealed class ForState private constructor() {
            companion object
        }
typealias StateOf<S, A> = Kind2<ForState, S, A>

typealias StatePartialOf<S> = Kind<ForState, S>

interface StateMonad<S> : Monad<StatePartialOf<S>>

---

https://github.com/arrow-kt/arrow-incubator/blob/main/arrow-recursion-data/src/main/kotlin/arrow/recursion/pattern/ListF.kt

sealed class ListF<A, B> : ListFOf<A, B> {
  class NilF<A, B> : ListF<A, B>()
  data class ConsF<A, B>(val a: A, val tail: B) : ListF<A, B>()

  fun <S> map(f: (B) -> S): ListFOf<A, S> = when (this) {
    is NilF -> NilF()
    is ConsF -> ConsF(a, f(tail))
  }

  companion object
}

typealias ListR<A> = Fix<ListFPartialOf<A>>

---

https://github.com/aedans/Katalyst/commit/99af4ed0bf1f4a8937e6243389d311190e871f3d

@higherkind
sealed class ListF<out F, out A> : ListFKind<F, A> {
    companion object
}

data class Cons<out F, out A>(val head: F, val tail: A) : ListF<F, A>()
object Nil : ListF<Nothing, Nothing>()

val <F, A> ListF<F, A>.either get() = when (this) {
    is Nil -> this.left()
    is Cons -> this.right()
}

@instance(ListF::class)
interface ListFFunctorInstance<F> : Functor<ListFKindPartial<F>> {
    override fun <A, B> map(fa: ListFKind<F, A>, f: (A) -> B) = fa.ev().either.fold({ Nil }, { Cons(it.head, f(it.tail)) })
}

typealias RList<T, A> = HK<T, ListFKindPartial<A>>

inline fun <reified T, A> List<A>.rList(): RList<T, A> = ana { if (it.isEmpty()) Nil else Cons(it.first(), it.drop(1)) }

inline val <reified T, A> RList<T, A>.list get(): List<A> =
    cata { it.ev().either.fold({ emptyList() }, { listOf(it.head) + it.tail }) }
 */

private fun <E> projectList(): (MyList<E>) -> MyListF<E, MyList<E>> =
  { list ->
    when (list) {
      is MyList.Nil -> MyOption.None
      is MyList.Cons -> MyOption(list.head to list.tail)
    }
  }

private fun <E> projectTodoList(): (MyList<E>) -> TodoMyListF<E, MyList<E>> =
  { list ->
    when (list) {
      is MyList.Nil -> TodoMyListF.TodoMyNil
      is MyList.Cons -> TodoMyListF.TodoMyCons(list.head, list.tail)
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

typealias Algebra<F, A> = (Kind<F, A>) -> A
typealias CoAlgebra<F, A> = (A) -> Kind<F, A>

// TODO continue

/*
val productOpA: Algebra[ListF[Int, ?], Int] = {
  case None => 1
  case Some((x, y)) => x * y
}
val rangeOpC: Coalgebra[ListF[Int, ?], Int] =
  n => if (n <= 0) None else Some((n, n - 1))


def cata[F[_]: Functor, S, B](algebra: Algebra[F, B])(project: Coalgebra[F, S]): S => B =
  new (S => B) { kernel =>
    def apply(input: S): B =
      algebra(project(input).fmap(kernel))
  }

def ana[F[_]: Functor, S, A](coalgebra: Coalgebra[F, A])(embed: Algebra[F, S]): A => S =
  new (A => S) { kernel =>
    def apply(init: A): S =
      embed(coalgebra(init).fmap(kernel))
  }

def projectListC[A]: Coalgebra[ListF[A, ?], List[A]] = {
  case Nil => None
  case head :: tail => Some((head, tail))
}

def embedListA[A]: Algebra[ListF[A, ?], List[A]] = {
  case None => Nil
  case Some((head, tail)) => head :: tail
}

cata(productOpA)(projectListC).apply(1 :: 10 :: 20 :: Nil) // 200: Int
ana(rangeOpC)(embedListA).apply(10) // List(10, 9, 8, 7, 6, 5, 4, 3, 2, 1): List[Int]
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

  fun step7() {
    // equivalent to Kind<ForMyOption, Pair<Int, Int>>
    // expected Kind<ForMyOption, Int>
    val prodListF: (TodoMyListFOf<Int, Int>) -> Int =
      { option ->
        when (val optionOfPair = option.fix()) {
          is TodoMyListF.TodoMyNil -> 1
          is TodoMyListF.TodoMyCons -> optionOfPair.head * optionOfPair.tail
        }
      }
    // FIXME
    println(foldRightStep7(prodListF, projectTodoList(), todo.intTodoMyListFFunctor)(MyList(1, 10, 20)))
  }
}

fun main() {
  // Steps.step1()
  // Steps.step2()
  Steps.step7()
}
