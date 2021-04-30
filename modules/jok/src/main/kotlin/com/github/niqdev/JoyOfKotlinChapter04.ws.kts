
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

// ------------------------------

// doubly recursive functions
// f(0) = 1
// f(1) = 1
// f(n) = f(n-1) + f(n-2)

fun fibonacciNaive(n: Int): Int =
  if (n == 0 || n == 1) 1 else fibonacciNaive(n - 1) + fibonacciNaive(n - 2)

fun fibonacciTailRecursive(n: Int): java.math.BigInteger {
  tailrec fun loop(i: Int, previous: java.math.BigInteger, current: java.math.BigInteger): java.math.BigInteger =
    when {
      i == 0 -> current
      else -> loop(i - 1, current, previous.add(current))
    }
  return loop(n, java.math.BigInteger.ZERO, java.math.BigInteger.ONE)
}

// 1 1 2 3 5 8 13 21 34 55
(0 until 10).map(::fibonacciNaive).forEach { print("$it ")}
(0 until 1000).map(::fibonacciTailRecursive).forEach { println(it)}
