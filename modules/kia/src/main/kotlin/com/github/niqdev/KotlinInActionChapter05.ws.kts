import java.lang.StringBuilder

// Lambda expressions, or simply lambdas, are essentially small chunks of code that can be passed to other functions

data class Person(
  val name: String,
  val age: Int,
)
val people = listOf(Person("Alice", 29), Person("Bob", 31))
// lambda expressions
// The code in curly braces { it.second } is a lambda implementing that logic. It receives a collection element as an argument (referred to using it) and returns a value to compare
// null ???
// `it` is an autogenerated parameter name
people.maxByOrNull { it.age }
// syntactic convention lets you move a lambda expression out of parentheses if it's the last argument in a function call
people.maxByOrNull { p: Person -> p.age }
// when the lambda is the only argument to a function, you can also remove the empty parentheses from the call
// parameter type explicitly written
people.maxByOrNull { p: Person -> p.age }
// parameter type inferred
people.maxByOrNull { p -> p.age }
// If you store a lambda in a variable, there's no context from which to infer the parameter types, so you have to specify them explicitly
val getAge = { p: Person -> p.age }
people.maxByOrNull(getAge)
// member references
people.maxByOrNull(Person::age)

// ------------------------------

// A lambda expression in Kotlin is always surrounded by curly braces
// Note that there are no parentheses around the arguments.
// The arrow separates the argument list from the body of the lambda

val sum = { x: Int, y: Int -> x + y }
println(sum(1, 2))

// call the lambda expression directly
val lambda: Unit = { println(42) }()

// if you need to enclose a piece of code in a block, you can use the library function run that executes the lambda passed to it
run { println(42) }

// ------------------------------

// >>> !!! Kotlin, unlike Java, allows you to access non-final variables and even modify them in a lambda !!!
// External variables accessed from a lambda are said to be "captured" by the lambda
// the lifetime of a local variable is constrained by the function in which the variable is declared
// But if it's captured by the lambda, the code that uses this variable can be stored and executed later
// When you capture a final variable, its value is stored together with the lambda code that uses it
// For non-final variables, the value is enclosed in a special wrapper that lets you change it, and the reference to the wrapper is stored together with the lambda

// ------------------------------

// convert the function to a value with the `::` operator
// member reference
val getAge0: kotlin.reflect.KProperty1<Person, Int> = Person::age

// a reference to a function that's declared at the top level (and isn't a member of a class)
fun hello() = println("hello")
run(::hello)

fun foo(
  a: String,
  b: Int,
): Boolean = true
val action: kotlin.reflect.KFunction2<String, Int, Boolean> = ::foo
val value: Boolean = action("a", 1)

data class Bar(
  val a: String,
  val b: Int,
)
// You can store or postpone the action of creating an instance of a class using a constructor reference
val bar: kotlin.reflect.KFunction2<String, Int, Bar> = ::Bar
val barInstance: Bar = bar("a", 1)

// bound member references, which allow you to use the member-reference syntax to capture a reference to the method on a specific object instance
val param = barInstance::b
param()

// ------------------------------

// all, any
// create intermediate collections "eagerly"
listOf(("aaa" to 1), ("bbb" to 2))
  .filter { it.first == "bbb" }
  .map { it.second * it.second }
  .all { it % 2 == 0 }

// ------------------------------

// The entry point for lazy collection operations in Kotlin is the Sequence interface
// The strength of the Sequence interface is in the way operations on it are implemented.
// The elements in a sequence are evaluated lazily
// As a rule, use a sequence whenever you have a chain of operations on a large collection
// Intermediate operations are always lazy
// The terminal operation causes all the postponed computations to be performed
// COLLECTION: apply all filter first, then map
// SEQUENCE: all operations are applied to each element sequentially: the first element is processed (filtered, then mapped), then the second element is processed, and so on
// The order of the operations you perform on a collection can affect performance as well
listOf(1, 2, 3)
  .asSequence()
  .filter { it % 2 == 0 } // intermediate operation
  .map { it + 1 } // intermediate operation
  .toList() // terminal operation

// ------------------------------

generateSequence(0) { it + 1 }.takeWhile { it <= 10 }.sum()

// ------------------------------

// Such interfaces are called functional interfaces, or SAM interfaces, where SAM stands for `single abstract method`.
// The Java API is full of functional interfaces like Runnable and Callable
// A SAM constructor is a compiler-generated function that lets you perform an explicit conversion of a lambda into an instance of a functional interface.

// Note that there's no this in a lambda as there is in an anonymous object: there's no way to refer to the anonymous class instance into which the lambda is converted.
// From the compiler's point of view, the lambda is a block of code, not an object, and you can't refer to it as an object. The this reference in a lambda refers to a surrounding class

// ------------------------------

// the ability to call methods of a different object in the body of a lambda without any additional qualifiers
// Such lambdas are called lambdas with receivers

// ------------------------------

// The `with` function converts its first argument into a `receiver` of the lambda that's passed as a second argument. You can access this receiver via an explicit this reference
// Note that an extension function is, in a sense, a function with a receiver
fun alphabet(): String =
  with(StringBuilder()) {
    for (letter in 'A'..'Z') {
      this.append(letter)
    }
    // omit `this`
    append("\nNow I know the alphabet!")
    // `this` is invoked on `StringBuilder`
    this.toString()
  }
println(alphabet())

// ------------------------------

// The `apply` function works almost exactly the same as `with`;
// the only difference is that apply always returns the object passed to it as an argument (in other words, the receiver object)
fun alphabet0(): String =
  StringBuilder()
    .apply {
      for (letter in 'A'..'Z') {
        this.append(letter)
      }
      // omit `this`
      append("\nNow I know the alphabet!")
    }.toString()
println(alphabet0())

// The `with` and `apply` functions are basic generic examples of using lambdas with receivers
// The `with` standard library function allows you to call multiple methods on the same object without repeating the reference to the object.
// `apply` lets you construct and initialize any object using a builder-style API
// see implementation of `buildString {  }`
