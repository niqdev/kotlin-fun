package com.github.niqdev

class MyIO(
  private val f: () -> Unit,
) {
  operator fun invoke(): Unit = f()

  // ---------- 12.4 ----------

  operator fun plus(io: MyIO): MyIO =
    MyIO {
      f()
      io.f()
    }

  companion object {
    val empty: MyIO = MyIO {}
  }
}

private fun show(message: String): MyIO = MyIO { println(message) }

private fun <A> toString(result: MyResult<A>): String = result.map<A, String> { it.toString() }.getOrElse { result.toString() }

private fun inverse(i: Int): MyResult<Double> =
  when (i) {
    0 -> MyResult.failure("Div by 0")
    else -> MyResult(1.0 / i)
  }

// ------------------------------

// TODO out ?
class IO<T>(
  private val f: () -> T,
) {
  operator fun invoke(): T = f()

  companion object {
    val empty: IO<Unit> = IO {}
    // operator fun <T> invoke(item: T): IO<T> = IO { item }
  }
}

// ---------- 12.5 ----------

object MyConsole {
  fun readLn(): IO<String> =
    IO {
      val br = java.io.BufferedReader(java.io.InputStreamReader(System.`in`))
      // TODO Either.catchNonFatal
      try {
        br.readLine()
      } catch (e: java.io.IOException) {
        throw IllegalStateException(e)
      }
    }

  fun printLn(out: Any): IO<Unit> = IO { println(out.toString()) }

  fun print(out: Any): IO<Unit> = IO { kotlin.io.print(out.toString()) }
}

// ---------- 12.6 ----------

fun <A, B> IO<A>.map(): ((A) -> B) -> IO<B> = { f -> IO { f(this()) } }

// ---------- 12.7 ----------

fun <A, B> IO<A>.flatMap(): ((A) -> IO<B>) -> IO<B> = { f -> f(this.invoke()) }

// ---------- 12.8 ----------

fun <A, B, C> IO<A>.map2(iob: IO<B>): ((A) -> (B) -> C) -> IO<C> = { f -> this.flatMap<A, C> { a -> iob.map<B, C> { b -> f(a)(b) } } }

fun <A> IO<A>.repeat(n: Int): IO<MyList<A>> =
  MyStream<IO<A>>()
    .fill(n, MyLazy { this })
    .foldRight<IO<A>, IO<MyList<A>>>()(MyLazy { IO { MyList() } }) { ioA ->
    { lazyIoListA ->
      ioA.map2<A, MyList<A>, MyList<A>>(lazyIoListA()) { a -> { la -> la.cons()(a) } }
    }
  }

// The repeat function, overflows the stack if the number of repetitions is too high.
// How much is too high depends on the stack size and how full it is when the program returned by the function runs.
// >>> The repeat function won't blow the stack. Only running the program it returns might do so!

// ---------- 12.9 ----------

// TODO IO stack-safe with trampolining
// https://github.com/pysaumont/fpinkotlin/blob/master/fpinkotlin-parent/fpinkotlin-effects-solutions/src/main/kotlin/com/fpinkotlin/effects/listing08/IO.kt

// ------------------------------

fun main() {
  val computation: MyIO = show(toString(inverse(3)))

  computation()

  fun getWorld(): String = "World"
  val instruction1 = MyIO { print("Hello ") }
  val instruction2 = MyIO { print(getWorld()) }
  val instruction3 = MyIO { print("!\n") }
  val script: MyIO = instruction1 + instruction2 + instruction3

  script()

  val programRight =
    MyList(instruction1, instruction2, instruction3)
      .foldRight<MyIO, MyIO>()(MyIO.empty) { io -> { io + it } }
  val programLeft =
    MyList(instruction1, instruction2, instruction3)
      .foldLeft<MyIO, MyIO>()(MyIO.empty) { acc, io -> acc + io }

  programRight()
  programLeft()

  fun buildMessage(name: String): String = "Hello, $name!"

  fun sayHelloMap(): IO<Unit> =
    MyConsole
      .print("Enter your name: ")
      .map<Unit, String> { MyConsole.readLn()() }
      .map<String, String> { buildMessage(it) }
      .map<String, Unit> { MyConsole.printLn(it)() }

  fun sayHelloFlatMap(): IO<Unit> =
    MyConsole
      .print("Enter your name: ")
      .flatMap<Unit, String> { MyConsole.readLn() }
      .map<String, String> { buildMessage(it) }
      .flatMap<String, Unit> { MyConsole.printLn(it) }

  val helloProgramMap = sayHelloMap()
  val helloProgramFlatMap = sayHelloFlatMap()
  helloProgramMap()
  helloProgramFlatMap()

  // lazy evaluation
  IO { println("repeat 3 times") }.repeat(3)
}
