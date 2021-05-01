import java.util.concurrent.ConcurrentHashMap

// `corecursion` is composing computation steps by using the output of one step as the input of the next one, starting with the first step
// `recursion` is the same operation but starts with the last step: computation must be delayed until the terminal condition is reached

// the main difference between recursion and corecursion is as follows
// - with corecursion, each step can be evaluated as soon as it's encountered
// - with recursion, all steps must be stored somewhere in some form
//   this allows delaying evaluation until the terminal condition is found
//   only then can each previous step be evaluated, in reverse order

// computer languages use the stack for storing computation steps
// this is smart because computation steps must be evaluated in reverse order compared to the order in which they were stacked
// unfortunately, the stack size is limited, so if there are too many steps, the stack will overflow, crashing the computation thread

// how many computation steps can safely be pushed onto the stack is language-dependent and can be configured
// in Kotlin, it's about 20,000; in Java, it's about 3,000

// the memory needed for the corecursive process is constant
// the memory needed for the recursive process grows with the number of steps

// corecursion
fun appendCharsToString(init: List<Char>): String {

  //fun append(s: String, c: Char): String = "$s$c"
  val append = { s: String, c: Char -> "$s$c" }

  /* tailrec */ fun loop(chars: List<Char>, result: String): String =
    when {
      chars.isEmpty() -> result
      // chars.drop(1) is equivalent to chars.subList(1, chars.size)
      // chars.first() is equivalent to chars[0]
      else -> loop(chars.drop(1), append(result, chars.first()))
    }

  return loop(init, "")
}

// recursion
fun prependCharsToString(list: List<Char>): String {
  fun prepend(c: Char, s: String): String = "$c$s"

  return if (list.isEmpty()) "" else prepend(list[0], prependCharsToString(list.subList(1, list.size)))
}

// ------------------------------

// it seems that corecursion would also exhaust the stack, although more slowly
// it is possible to completely eliminate the problem
// the trick is to transform a corecursive function into a good old loop

// unlike Java, Kotlin implements Tail Call Elimination (TCE)
// this means that when a function call to itself is the last thing the function does (meaning that the result of this call isn't used in a further computation), Kotlin eliminates this tail call

// you must indicate that this is your intention
// >>> `tailrec fun loop`

// ------------------------------

// sum of integers from 1 to 10

fun sumRecursion(n: Int): Int =
  if (n < 1) 0 else n + sumRecursion(n - 1)

fun sumImperative(n: Int): Int {
  var result = 0
  var i = 0
  // Should it be `<=` or `<`? Should `i` be incremented before or after `result`?
  while(i <= n) {
    result += i
    // increment after
    i++
  }
  return result
}

fun sumCoRecursion(n: Int): Int {
  tailrec fun loop(i: Int, result: Int): Int =
    when {
      i > n -> result
      else -> loop(i + 1, i + result)
    }
  return loop(0, 0)
}

sumRecursion(10)
sumImperative(10)
sumCoRecursion(10)

// ---------- 4.1 ----------

// implement a corecursive `add` function working with positive integers without using + or -
fun add(a: Int, b: Int): Int {
  fun inc(n: Int) = n + 1
  fun dec(n: Int) = n - 1

  tailrec fun loop(i: Int, result: Int): Int =
    when {
      i > 0 -> loop(dec(i), inc(result))
      else -> result
    }

  return loop(b, a)
}
add(5, 3)

//tailrec fun addAlternative(a: Int, b: Int): Int = if (b == 0) a else add(inc(a), dec(b))

// ---------- 4.2 ----------

fun factorialRecursion(n: Int): Int = if (n == 0) 1 else n * factorialRecursion(n - 1)

fun factorialCoRecursion(n: Int): Int {
  fun loop(i: Int, result: Int): Int =
    when(i) {
      0 -> result
      else -> loop(i - 1, i * result)
    }
  return loop(n, 1)
}

// write a recursive factorial value function
object Factorial {
  // lazy initialization (1)
  // lazy { ... }
  val factorialValueRecursion0: (Int) -> Int by lazy {
    { n -> if (n == 0) 1 else n * factorialValueRecursion0(n - 1) }
  }

  // lazy initialization (2)
  // use private lateinit + init constructor
  private lateinit var factorialValueRecursionInternal: (Int) -> Int
  init {
    factorialValueRecursionInternal = { n -> if (n == 0) 1 else n * factorialValueRecursionInternal(n - 1) }
  }
  val factorialValueRecursion1 = factorialValueRecursionInternal
}

// Kotlin Int values are 32-bit numbers: arithmetic overflow, producing a negative result
factorialRecursion(5)
factorialCoRecursion(5)
Factorial.factorialValueRecursion0(5)
Factorial.factorialValueRecursion1(5)

// ------------------------------

fun <T> List<T>.head(): T =
  if (isEmpty()) throw IllegalArgumentException("empty") else this[0]

fun <T> List<T>.tail(): List<T> =
  if (isEmpty()) throw IllegalArgumentException("empty") else drop(1)

fun List<Int>.sum(): Int {
  tailrec fun loop(tmp: List<Int>, result: Int): Int =
    when {
      tmp.isEmpty() -> result
      else -> loop(tmp.tail(), result + tmp.head())
    }
  return loop(this, 0)
}
listOf(1, 2, 3).sum()

// ---------- 4.3 ----------

// doubly recursive functions
// f(0) = 1
// f(1) = 1
// f(n) = f(n-1) + f(n-2)

fun fibonacciNaive(n: Int): Int =
  if (n == 0 || n == 1) 1 else fibonacciNaive(n - 1) + fibonacciNaive(n - 2)

fun fibonacciTailRecursive(n: Int): java.math.BigInteger {
  tailrec fun loop(i: Int, previous: java.math.BigInteger, current: java.math.BigInteger): java.math.BigInteger =
    when(i) {
      0 -> current
      else -> loop(i - 1, current, previous.add(current))
    }
  return loop(n, java.math.BigInteger.ZERO, java.math.BigInteger.ONE)
}

// 1 1 2 3 5 8 13 21 34 55
(0 until 10).map(::fibonacciNaive).forEach { print("$it ")}
(0 until 1000).map(::fibonacciTailRecursive).forEach { println(it)}

// ---------- 4.4 ----------

fun <T> makeString(list: List<T>, delim: String): String =
  when {
    list.isEmpty() -> ""
    list.tail().isEmpty() -> "${list.head()}${makeString(list.tail(), delim)}"
    else -> "${list.head()}$delim${makeString(list.tail(), delim)}"
  }

// write a tail-recursive version of the makeString function

fun <T> makeStringRecursive(list: List<T>, delim: String): String  {
  tailrec fun loop(tmp: List<T>, result: String): String =
    when {
      tmp.isEmpty() -> result
      result == "" -> loop(tmp.tail(), "${tmp.head()}")
      else -> loop(tmp.tail(), "$result$delim${tmp.head()}")
    }
  return loop(list, "")
}

makeString(listOf(1, 2, 3, 4), " - ")
makeStringRecursive(listOf(1, 2, 3, 4), " - ")

// ---------- 4.5 ----------

fun <I, O> List<I>.myFoldLeft(): (O) -> ((O, I) -> O) -> O =
  { zero -> { f ->
    tailrec fun loop(tmp: List<I>, accumulator: O): O =
      when {
        tmp.isEmpty() -> accumulator
        else -> loop(tmp.tail(), f(accumulator, tmp.head()))
      }
    loop(this, zero)
  }}

listOf(1, 2, 3).myFoldLeft<Int, Int>()(0)() { acc, i -> acc + i }
listOf(1, 2, 3).myFoldLeft<Int, String>()("")(String::plus)

// ---------- 4.6 ----------

fun <I, O> List<I>.myFoldRight(): (O) -> ((I, O) -> O) -> O =
  { zero -> { f ->
    when {
      isEmpty() -> zero
      else -> f(head(), tail().myFoldRight<I, O>()(zero)(f))
    }
  }}

listOf(1, 2, 3).myFoldRight<Int, String>()("")() { int, acc -> "$int$acc" }

// ---------- 4.7-8 ----------

fun <T> List<T>.myReverse(): List<T> {
  val result = mutableListOf<T>()
  (this.size downTo 1).forEach { result.add(this[it - 1]) }
  return result
}

// define a reverse function using a fold

fun <T> List<T>.myReverseFoldLef(): List<T> =
  this.myFoldLeft<T, List<T>>()(listOf())() { acc, t -> listOf(t) + acc }

fun <T> List<T>.myReverseFoldRight(): List<T> =
  this.myFoldRight<T, List<T>>()(listOf())() { t, acc -> acc + t }

listOf(1, 2, 3).myReverse()
listOf(1, 2, 3).myReverseFoldLef()
listOf(1, 2, 3).myReverseFoldRight()

// ---------- 4.9-12 ----------

fun rangeImperative(start: Int, end: Int): List<Int> {
  var index = start
  val result = mutableListOf<Int>()
  while (index < end) {
    result.add(index)
    index ++
  }
  return result
}

fun rangeCoRecursive(start: Int, end: Int): List<Int> {
  tailrec fun loop(index: Int, result: List<Int>): List<Int> =
    when {
      index >= end -> result
      else -> loop(index + 1, result + index)
    }
  return loop(start, listOf())
}

rangeImperative(0, 8)
rangeCoRecursive(0, 8)

// ---------- 4.10-13-14 ----------

//fun <T> myUnfold(seed: T, f: (T) -> T, p: (T) -> Boolean): List<T> = TODO()

fun <T> myUnfold(): (T) -> ((T) -> T) -> ((T) -> Boolean) -> List<T> =
  { seed -> { f -> { p ->
    tailrec fun loop(current: T, result: List<T>): List<T> =
      when {
        !p(current) -> result
        else -> loop(f(current), result + current)
      }
    loop(seed, listOf())
  }}}

myUnfold<Int>()(0)() { i -> i + 1 }() { i -> i < 8 }

// ---------- 4.11 ----------

fun rangeUnfold(start: Int, end: Int): List<Int> =
  myUnfold<Int>()(start)() { it + 1 }() { it < end }

rangeUnfold(0, 8)

// ------------------------------

// `memoization` is the technique of keeping the result of a computation in memory so it can be returned immediately if you have to redo the same computation in the future

fun fibonacciNaiveMemoization(n: Int): List<java.math.BigInteger> =
  when {
    n < 1 -> throw IllegalArgumentException("???")
    n == 1 -> listOf(java.math.BigInteger.ONE)
    else -> {
      var fibonacci1 = java.math.BigInteger.ONE
      var fibonacci2 = java.math.BigInteger.ONE
      var fibonacci: java.math.BigInteger
      val result = mutableListOf<java.math.BigInteger>(java.math.BigInteger.ONE, java.math.BigInteger.ONE)

      for (i in 2 until n) {
        fibonacci = fibonacci1.add(fibonacci2)
        result.add(fibonacci)
        // memoization: computes only once
        fibonacci1 = fibonacci2
        fibonacci2 = fibonacci
      }

      result
    }
  }

fibonacciNaiveMemoization(100)

// ---------- 4.15 ----------

fun fibonacciCoRecursiveMemoization(n: Int): List<java.math.BigInteger> {

  fun loop(
    index: java.math.BigInteger,
    fibonacci1: java.math.BigInteger,
    fibonacci2: java.math.BigInteger,
    result: List<java.math.BigInteger>
  ): List<java.math.BigInteger> =
    when(index) {
      java.math.BigInteger.ZERO -> result
      java.math.BigInteger.ONE -> result + fibonacci1.add(fibonacci2)
      else -> loop(
        index = index - java.math.BigInteger.ONE,
        fibonacci1 = fibonacci2,
        fibonacci2 = fibonacci1.add(fibonacci2),
        result = result + fibonacci1.add(fibonacci2)
      )
    }

  return loop(
    java.math.BigInteger.valueOf(n.toLong()),
    java.math.BigInteger.ONE,
    java.math.BigInteger.ONE,
    listOf<java.math.BigInteger>(java.math.BigInteger.ONE, java.math.BigInteger.ONE)
  )
}

fibonacciCoRecursiveMemoization(100)

// ------------------------------

// instead of a suite of numbers, you could see the Fibonacci series as a suite of pairs
// 1, 1, 2, 3, 5, 8, 13, 21, ...
// (1, 1), (1, 2), (2, 3), (3, 5), (5, 8), (8, 13), (13, 21), ...
// each tuple can be constructed from the previous one
// - the second element of tuple `n` becomes the first element of tuple `n + 1`
// - the second element of tuple `n + 1` is equal to the sum of the two elements of tuple `n`

// ---------- 4.16 ----------

fun <T> myIterate(): (T) -> ((T) -> T) -> (Int) -> List<T> =
  { seed -> { f -> { count ->
    tailrec fun loop(index: Int, current: T, result: List<T>): List<T> =
      when {
        index >= count -> result
        else -> loop(index + 1, f(current), result + current)
      }
    loop(0, seed, listOf())
  }}}

myIterate<Int>()(0)() { i -> i + 1 }(5)

// ---------- 4.17 ----------

fun <I, O> List<I>.myMap(): ((I) -> O) -> List<O> =
  { f ->
    tailrec fun loop(tmp: List<I>, result: List<O>): List<O> =
      when {
        tmp.isEmpty() -> result
        else -> loop(tmp.tail(), result + f(tmp.head()))
      }
    loop(this, listOf())
  }

listOf(1, 2, 3).myMap<Int, Int>()() { it + 5 }

// ---------- 4.18 ----------

fun fibonacciComposition(n: Int): String {
  val seed = Pair(java.math.BigInteger.ZERO, java.math.BigInteger.ONE)
  val f = { (a, b): Pair<java.math.BigInteger, java.math.BigInteger> -> Pair(b, a + b) }
  val listOfPairs = myIterate<Pair<java.math.BigInteger, java.math.BigInteger>>()(seed)(f)(n + 1)
  val list = listOfPairs.myMap<Pair<java.math.BigInteger, java.math.BigInteger>, java.math.BigInteger>()() { it.first }
  return makeStringRecursive(list, " - ")
}

fibonacciComposition(10)

// ------------------------------

object Doubler {
  private val cache = mutableMapOf<Int, Int>()
  fun double(x: Int): Int = cache.computeIfAbsent(x) { it * 2 }
}

class Memoizer<T, U> private constructor() {
  private val cache = ConcurrentHashMap<T, U>()

  private fun doMemoize(function: (T) -> U): (T) -> U =
    { input -> cache.computeIfAbsent(input) { function(it) } }

  companion object {
    // returns a memoized version of its function argument
    fun <T, U> memoize(function: (T) -> U): (T) -> U =
      Memoizer<T, U>().doMemoize(function)
  }
}

// memoizing is about maintaining state between function calls
