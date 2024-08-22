/*
// requires kotlin-script-runtime to run
dependencies {
  implementation(kotlin("script-runtime"))
}
*/

println("hello")

// ------------------------------

// elvis operator ?:

// when you use lambdas, many of the Kotlin standard library functions will inline them.
// Inlining lambdas ensures that no new objects will be created and the application won’t suffer from extra GC pauses.

// nullable: String? = null

// check the type (casting) value is String

// Code compiled with the Kotlin compiler depends on the Kotlin runtime library
// It contains the definitions of Kotlin’s own standard library classes and the extensions that Kotlin adds to the standard Java APIs. The runtime library needs to be distributed with your application
// kotlinc <source file or directory> -include-runtime -d <jar name>
// java -jar <jar name>

// REPL kotlinc

// ------------------------------

// In Java, all control structures are statements.
// In Kotlin, most control structures, except for the loops (for, do, and do/ while) are expressions.
// assignments are expressions in Java and become statements in Kotlin

// function: {} block body + return keyword
fun maxFunction(
  a: Int,
  b: Int,
): Int {
  // `if` is an expression, not a statement
  return if (a > b) a else b
}

// function with expression body: one single expression only
// expression: NO {}, NO return
fun maxExpression(
  a: Int,
  b: Int,
): Int = if (a > b) a else b

// functions with a block body that return a value must specify the return type and write the return statements explicitly
// omitting the return type is allowed only for functions with an expression body
maxFunction(1, 2)
println("max ${maxExpression(2, 1)}")

// ------------------------------

// value objects
// `public` is the default visibility
class Person(
  val name: String,
)

// In Java, the combination of the field and its accessors (getter and setter) is often referred to as a property

// Kotlin doesn't make a distinction between importing classes and functions,
// and it allows you to import any kind of declaration using the import keyword.
// You can import the top-level function by name.

// ------------------------------

// enum is a so-called soft keyword: it has a special meaning when it comes before class,
// but you can use it as a regular name in other places
enum class Color(
  val r: Int,
  val g: Int,
  val b: Int,
) {
  RED(255, 0, 0),
  GREEN(0, 255, 0),
  BLUE(0, 0, 255),
  ;

  fun rgb() = (r * 256 + g) * 256 + b
}
println(Color.BLUE.rgb())

// expression
fun getColorName(color: Color): String =
  when (color) {
    // combine multiple e.g.
    // Color.RED, Color.GREEN -> "myColor"
    Color.RED -> "red"
    Color.GREEN -> "green"
    Color.BLUE -> "blue"
  }
println(getColorName(Color.BLUE))

fun getColorSet(
  c1: Color,
  c2: Color,
): String =
  when (setOf(c1, c2)) {
    setOf(Color.RED, Color.GREEN) -> "myColor"
    else -> TODO()
  }

// ------------------------------

// `:` class implements an interface
interface Expr

class Num(
  val value: Int,
) : Expr

class Sum(
  val left: Expr,
  val right: Expr,
) : Expr

// `is` is called smart cast
// `is` check is similar to instanceof in Java
// in Java you need to add an explicit cast
// java-like: `e is Num` and `e as Num`
fun eval(e: Expr): Int =
  when (e) {
    is Num -> e.value
    is Sum -> eval(e.left) + eval(e.right)
    else -> TODO()
  }
eval(Sum(Num(1), Num(2)))

// ------------------------------

// ranges in are closed or inclusive, meaning the second value is always a part of the range
fun fizzBuzz(i: Int): String =
  when {
    i % 15 == 0 -> "FizzBuzz "
    i % 3 == 0 -> "Fizz "
    i % 5 == 0 -> "Buzz "
    else -> "$i "
  }
// `for (x in 0 until size)` is equivalent to `for (x in 0..size-1)`
for (i in 1..100) {
  print(fizzBuzz(i))
}
for (i in 100 downTo 1 step 2) {
  print(fizzBuzz(i))
}

// ------------------------------

// keys are sorted
val binaryMap = java.util.TreeMap<Char, String>()

for (c in 'A'..'F') {
  val binary = Integer.toBinaryString(c.toInt())
  binaryMap[c] = binary
}
for ((letter, binary) in binaryMap) {
  println("$letter = $binary")
}
val binaryArray = arrayListOf("10", "11", "1001")
for ((index, element) in binaryArray.withIndex()) {
  println("$index: $element")
}

fun isLetter(c: Char) = c in 'a'..'z' || c in 'A'..'Z'

fun isNotDigit(c: Char) = c !in '0'..'9'
println(isLetter('q'))
println(isNotDigit('x'))

// uses java.lang.Comparable
fun recognize(c: Char) =
  when (c) {
    in '0'..'9' -> "digit"
    in 'a'..'z', in 'A'..'Z' -> "letter"
    else -> "unknown"
  }

// ------------------------------

// throw construct is an expression and can be used as a part of other expressions
// throw IllegalArgumentException("BAD")

// checked exceptions in Java need to be handled explicitly
// Kotlin doesn't differentiate between checked and unchecked exceptions
// java-like: try/catch/finally

// `try` keyword in Kotlin is an expression
// unlike with if, you always need to enclose the statement body in curly braces
