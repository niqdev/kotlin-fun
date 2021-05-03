// the word `effects` means all interactions with the outside world, such as writing to the console, to a file, to a database, or to a network, and also mutating any element outside the component's scope
// - programming with side effects is wrong in all cases
// - safe programs are built by composing functions that take an argument and return a value: `single responsibility` principle
// when a method or function returns a value and has an effect, this is called a `side effect`
// a function without side effects is called `pure function`
// code that neither mutates nor depends on the external world is said to be `referentially transparent`
// `substitution model`: it's always possible to replace a function call or any referentially transparent expression with its value

// ------------------------------

fun getValue() = "hello"

// invoked only once when referenced
val lazyName1: String by lazy { getValue() }
val lazyName2: String by lazy(::getValue)

// lazy initialization can't be used for mutable references
lateinit var mutableName: String
mutableName = getValue()

// ------------------------------

// a Kotlin class is `public` by default: to make a class non-public, you can use the `private`, `protected`, or `internal` modifiers
// - `internal` modifier means that the class is accessible only from inside the module where it's defined
// - `protected` is restricted to extending classes and doesn't include classes in the same package
// a Kotlin class is `final` by default: to make a Kotlin class non-final, use the open modifier
class Person0 constructor(name: String) {
  val name: String
  // constructor implementation with access to constructor parameters
  init {
    this.name = name
  }
}
// equivalent to
class Person1 constructor(name: String) {
  // no `init` block: one-liner constructor
  val name: String = name
}
// equivalent to (combine the constructor declaration, the property declaration, and the property initialization)
// added `val`
class Person2 constructor(val name: String) // with {} (formatKotlin removed it)
// equivalent to (because the block {} is empty, it can be removed and also the word `constructor`)
class Person3(val name: String)

// ------------------------------

// must be declared `open for extension`
open class Person4(val name: String) : java.io.Serializable, Comparable<Person4> {

  // when extending a class or implementing an interface overriding must be explicitly specified
  override fun compareTo(other: Person4): Int =
    TODO("Not yet implemented")
}
// NO `val`
class Member(name: String, val age: Int) : Person4(name)
// use the constructor as a function to create a new instance
val person: Person4 = Member("myName", 42)

// ------------------------------

// generates `hashCode`, `equals`, `toString`, `copy` and `componentN` functions
data class Person5(val name: String, val age: Int = 42)

val person5 = Person5("name")

// destructuring data objects
println("${person5.component1()}==${person5.name} | ${person5.component2()}==${person5.age}")

val (name5) = person5
val (_, age5) = person5
println("$name5 | $age5")

// ------------------------------

// in Kotlin, classes have no static members: use `companion objects`
data class Person6(val name: String) {

  companion object {
    fun hello(value: String) = println("hello $value")
  }
}
Person6.hello("AAA")
Person6.Companion.hello("AAA")

// ------------------------------

// singletons: in Java utility classes that contain only static methods with forbidden class instantiation
object Person7 {
  fun hello(value: String) = println("hello $value")
}

// ------------------------------

// Kotlin has no primitives

// as in Java, you can use underscores in numbers
// - Longs have a trailing L, and floats a trailing F
// - Doubles are distinguished by the use of a decimal dot such as 2.0, or .9
// - Hexadecimal values must be prefixed with 0x e.g. 0xBE_24_1C_D3
// - Binary literals are prefixed with 0b: 0b01101101_11001010_10010011_11110100

// ------------------------------

// Kotlin collections are backed by Java collections and has 2 types: mutable and immutable (read-only)
// By default, Kotlin collections are immutable

// The + operator is an infix extension function called plus, which Kotlin declares in the Collection interface that List extends and it's translated into a static function

val immutableList = listOf(1, 2, 3)
immutableList + 8 + listOf(42)

val mutableList = mutableListOf(1, 2, 3)
mutableList.add(6)
mutableList.addAll(mutableListOf(42))

// ------------------------------

// - functions can be declared at the package level
// - packages don't have to correspond to the directory structure where they're stored
// - classes don't need to be defined in files by the same names
// - there's no notion of a subpackage (packages containing packages)
// - filenames are irrelevant as long as they have a .kt extension

// ------------------------------

// - functions and properties can be defined at the package level, not only in classes
// - all elements defined at the package level are public by default
// - if an element is declared private, it's only visible from inside the same file
// - an element declared `internal` means that it's only visible from inside the same module
// - a module is a set of files compiled together: a Gradle source set, an IntelliJ module. A module is intended to be packed into a single jar file

// ------------------------------

fun myClosure(limit: Int): Int {

  val seq = listOf(1, 2, 3)

  // local inner function
  // inc() function `close over` the "seq" variable: such a construct is called a `closure`
  // multi-line lambda
  fun inc(): Int = seq.fold(
    0,
    { acc, i ->
      val result = acc + i
      // the value returned by the lambda is the value of the expression on the last line
      if (result < limit) result else acc
    }
  )

  return inc()
}

myClosure(2)

// ------------------------------

// `extension functions` are functions that can be called on objects as if they were instance functions of the corresponding class

fun <T> List<T>.myLength(): Int = this.size

listOf(1, 2, 3).size
// parentheses are mandatory
listOf(1, 2, 3).myLength()

// ------------------------------

// `lambdas` are anonymous functions, meaning function implementations that aren't referenced by a name

// lambdas are included between curly braces
fun triple(list: List<Int>): List<Int> = list.map({ a -> a * 3 })
// when the lambda is the last argument of a function, it can be put outside of the parentheses
fun product(list: List<Int>): Int = list.fold(1) { acc: Int, value: Int -> acc * value }

// simplified syntax for lambdas with a single parameter implicitly named `it`
fun List<Int>.myTriple0(): List<Int> = this.map { it * 3 }
fun List<Int>.myTriple1(): List<Int> = map { it * 3 }

// ------------------------------

// `Int` is said to be a non-nullable type, whereas `Int?` is a nullable type
// any non-nullable type is a child type of the corresponding nullable type

val nonNullable: Int = 3
val nullable: Int? = nonNullable
// this doesn't compile
// val nonNullableFromNullable: Int = nullable

val nullableString: String? = "maybeNull"
// the dot "." is called `dereferencing operator` and can't be used here because it could cause an NPE (NullPointerException)
// this doesn't compile
// val size = nullableString.length

// Kotlin make sure there is a NULL check
val size0 = if (nullableString != null) nullableString else null
// "?." is called `safe call` operator
val size1 = nullableString?.length

// or take full responsibility: it can throw NPE
val size2 = nullableString!!.length

// "?:" is called elvis operator (equivalent of ternary operator or getOrElse)
val size3 = nullableString?.length ?: 0

// ------------------------------

// the "if...else" construct is an expression that can be evaluated to a value

// "when" replaces "switch...case"
val myValue = "aaa"
val verifyValue0 = when (myValue) {
  "aaa" -> "A"
  "bbb" -> "B"
  "ccc" -> "C"
  else -> "XYZ"
}
val verifyValue1 = when {
  myValue.startsWith("a") -> "A"
  myValue.endsWith("b") -> "B"
  true -> "C"
  else -> "XYZ"
}

// ------------------------------

// loops: replace control structures with functions saying "what" should be done (not how it should be done)
// an indexed loop is an iteration over a collection of indexes
for (i in 0 until 10 step 2) println(i)

// ------------------------------

// Kotlin has no checked exceptions: all exceptions are unchecked
// "try..catch..finally" construct is an expression returning a value

// ------------------------------

// automatically close resources in Java with the "try with resource" construct, provided these resources implement either Closable or AutoClosable
// Kotlin offers the `use` function

// val lines= java.nio.file.Files.newInputStream(java.nio.file.Paths.get("myFile.txt")).use {
//  it.bufferedReader().lineSequence()
// }
// lines.forEach(::println)

// alternative
// java.io.File("myFile.txt").forEachLine { println(it) }
// java.io.File("myFile.txt").useLines{ it.forEach(::println) }

// ------------------------------

val anyValue: Any = "myString"
// smart casts with "is" keyword
if (anyValue is String) "STRING" else "BHO"
// unsafe cast with "as" keyword: throws ClassCastException
val unsafeString: String = anyValue as String
// if the cast doesn't succeed, the result will be null instead of an exception
val nullableUnsafeString: String? = anyValue as? String

// ------------------------------

// one of the classical pitfalls in Java is the possible confusion between equality and identity
// Java returns a memoized shared version of integers for low values, so "Integer.valueOf(1)" always returns the same object, whereas "Integer.valueOf(512)" returns a new distinct object on each call
// see example pag. 40
// To be safe in Java, always test equality with `equals` for objects and with `==` for primitives
// The `==` symbol tests equality for primitives and identity for objects

// in Kotlin:
// "identity" (also called "referential equality") is tested with `===`
// "equality" (sometimes called "structural equality") is tested with `==`, which is a shorthand for "equals"

// ------------------------------

// `variance` describes how parameterized types behave in relation to subtyping
// OUT: `covariance` means that a List<Red> is considered a subtype of List<Color> if Red is a subtype of Color: List<T> is said to be a "covariant" on T
// IN: if List<Color> is considered a subtype of List<Red>, then List<T> is said to be a `contravariant` on T
// the absence of a keyword means `invariant`

val myListString: List<String> = listOf("string")
// adding an Int element to an "immutable list" of strings produces a new list of type List<Any>, without changing the type of the original list
val myListAny: List<Any> = myListString + 42
