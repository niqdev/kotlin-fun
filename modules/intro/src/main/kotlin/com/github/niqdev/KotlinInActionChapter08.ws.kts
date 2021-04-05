// By definition, a "higher-order" function is a function that takes another function as an argument or returns one
// In Kotlin, functions can be represented as values using lambdas or function references

val sum0 = { x: Int, y: Int -> x + y }
val action0 = { println(42) }

val sum1: (Int, Int) -> Int = { x, y -> x + y }
val action1: () -> Unit = { println(42) }

// a function that returns a nullable type
val nullable: () -> Int? = { null }

// nullable variable of a function type
val funOrNUll: ((Int, Int) -> Int)? = null

// ------------------------------

// String: receiver type
// predicate: parameter name
// (String) -> Boolean: parameter function type
// (String): parameter type of function passed as parameter
// Boolean: return type of function passed as parameter
fun String.myFilter(predicate: (Char) -> Boolean): String {
  val sb = java.lang.StringBuilder()
  for (index in 0 until this.length) {
    val element = this.get(index)
    if (predicate(element)) sb.append(element)
  }
  return sb.toString()
}
println("ab1c".myFilter { it in 'a'..'z' })

// ------------------------------

enum class Delivery { STANDARD, PREMIUM }

class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double =
  if (delivery == Delivery.PREMIUM) { order -> 6 + 2.1 * order.itemCount }
  else { order -> 1.2 * order.itemCount }

val shippingCosts = getShippingCostCalculator(Delivery.PREMIUM)(Order(3))

// ------------------------------

// lambdas are normally compiled to anonymous classes
// every time you use a lambda expression, an extra class is created; and if the lambda captures some variables, then a new object is created on every invocation.
// This introduces runtime overhead
// If you mark a function with the `inline` modifier, the compiler won't generate a function call when this function is used and instead will replace every call to the function with the actual code implementing the function
// When you declare a function as inline, its body is inlined—in other words, it’s substituted directly into places where the function is called instead of being invoked normally
// The bytecode generated from the lambda becomes part of the definition of the calling function and isn’t wrapped in an anonymous class implementing a function interface

inline fun applyMyAction(action: () -> Boolean): String = TODO()

// not every function that uses lambdas can be inlined. When the function is inlined, the body of the lambda expression that's passed as an argument is substituted directly into the resulting code
// Generally, the parameter can be inlined if it's called directly or passed as an argument to another inline function

inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit): String = TODO()

// Using the inline keyword is likely to improve performance only with functions that take lambdas as arguments; all other cases require additional measuring and investigation

// ------------------------------

// One common pattern where lambdas can remove duplicate code is resource management: acquiring a resource before an operation and releasing it afterward
fun readFirstLineFromFile(path: String): String =
  java.io.BufferedReader(java.io.FileReader(path)).use { it.readLine() }

// ------------------------------

// local return with a label

fun lookForAlice0(people: List<String>) {
  // label@: lambda label
  // return@label: return expression label
  people.forEach label@{
    if (it == "Alice") return@label
  }
  println("after")
}

// Alternatively, the name of the function that takes this lambda as an argument can be used as a label
fun lookForAlice1(people: List<String>) {
  people.forEach {
    if (it == "Alice") return@forEach
  }
  println("after")
}

// Anonymous functions: local returns by default
fun lookForAlice2(people: List<String>) {
  people.forEach(
    fun (person) {
      if (person == "Alice") return
      println("$person is not Alice")
    }
  )
}

// an anonymous function looks similar to a regular function, except that its name and parameter types are omitted
fun lookForAlice3(people: List<Int>) {
  people.filter(
    fun (person): Boolean {
      return person < 30
    }
  )
}

// See 8.5

// >>> The rule is simple: return returns from the closest function declared using the fun keyword.
// Lambda expressions don't use the fun keyword, so a return in a lambda returns from the outer function.
// Anonymous functions do use fun; therefore, the anonymous function is the closest matching function. Consequently, the return expression returns from the anonymous function, not from the enclosing one
