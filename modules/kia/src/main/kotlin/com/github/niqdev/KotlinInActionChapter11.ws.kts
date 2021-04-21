import java.lang.StringBuilder

// Kotlin DSLs are fully statically typed

// general-purpose programming language: a set of capabilities complete enough to solve essentially any problem that can be solved with a computer
// domain-specific language: focuses on a specific task, or domain, and forgoes the functionality that's irrelevant for that domain

// DSLs example: SQL and regular expressions

// DSLs tend to be declarative: whereas an `imperative language` describes the exact sequence of steps required to perform an operation,
// a `declarative language` describes the desired result and leaves the execution details to the engine that interprets it

// As opposed to `external DSLs`, which have their own independent syntax, `internal DSLs` are part of programs written in a general-purpose language, using exactly the same syntax

// A typical library consists of many methods, and the client uses the library by calling the methods one by one.
// There's no inherent structure in the sequence of calls, and no context is maintained between one call and the next. Such an API is sometimes called a `command-query` API.
// As a contrast, the method calls in a DSL exist in a larger `structure`, defined by the `grammar` of the DSL
// This grammar is what allows us to call an internal DSL a `language`

// In a Kotlin DSL, structure is most commonly created through the nesting of lambdas or through chained method calls

// ------------------------------

// lambdas with receivers: are a powerful Kotlin feature that allows you to build APIs with a structure

fun buildString(builderAction: (StringBuilder) -> Unit): String {
  val sb = StringBuilder()
  builderAction(sb)
  return sb.toString()
}

val s0 = buildString {
  // "it" refer to StringBuilder instance
  it.append("Hello")
  it.append(" world!")
}
println(s0)

// ------------------------------

// to replace "it.append" with "append" you need to convert the lambda into a `lambda with a receiver`
// give one of the parameters of the lambda the special status of a `receiver`, letting you refer to its members directly without any qualifier

// ".()" declares a parameter of a function type with a receiver
// use an extension function type instead of a regular function type to declare the parameter type
// "StringBuilder.() -> Unit" type is called the `receiver type`, and the value of that type passed to the lambda becomes the `receiver object`
fun buildStringWithReceiver(builderAction: StringBuilder.() -> Unit): String {
  val sb = StringBuilder()
  // passes a StringBuilder as a receiver to the lambda
  // the way you invoke the variable also changes when you convert it from a regular function type to an extension function type.
  // Instead of passing the object as an argument, you invoke the lambda variable as if it were an extension function
  // builderAction here isn't a method declared on the StringBuilder class; it's a parameter of a function type that you call using the same syntax you use to call extension functions
  sb.builderAction()
  return sb.toString()
}

val s1 = buildStringWithReceiver {
  this.append("Hello")
  append(" world")
}
println(s1)

// an extension function type
// String.(Int, Int) -> Unit
// - String: receiver type
// - (Int, Int): parameter type
// - Unit: return type

// a lambda with a receiver looks exactly the same as a regular lambda in the source code.
// To see whether a lambda has a receiver, you need to look at the function to which the lambda is passed:
// its signature will tell you whether the lambda has a receiver and, if it does, what its type is

// "appendExclamationMark" is a value of an extension function type
val appendExclamationMark: StringBuilder.() -> Unit =
  { this.append("!") }

val stringBuilder = StringBuilder("Hi")
stringBuilder.appendExclamationMark()
println(stringBuilder)

println(buildStringWithReceiver(appendExclamationMark))

// ------------------------------

// all `apply` and `with` do is invoke the argument of an extension function type on the provided receiver

inline fun <T> T.myApply(block: T.() -> Unit): T {
  block()
  return this
}

inline fun <T, R> myWith(receiver: T, block: T.() -> R): R =
  receiver.block()

val map = mutableMapOf(1 to "one")
map.myApply { this[2] = "two"}
myWith(map) { this[3] = "three" }
println(map)

// ------------------------------

// example of https://github.com/Kotlin/kotlinx.html

// A Kotlin DSL for HTML is usually called an HTML builder, and it represents a more general concept of type-safe builders
// Builders provide a way to create an object hierarchy in a declarative way

open class Tag(val name: String) {
  private val children = mutableListOf<Tag>()

  protected fun <T: Tag> doInit(child: T, init: T.() -> Unit) {
    child.init()
    children.add(child)
  }

  override fun toString(): String =
    "<$name>${children.joinToString("")}</$name>"
}

fun table(init: TABLE.() -> Unit) =
  TABLE().apply(init)

class TABLE: Tag("table") {
  fun tr(init: TR.() -> Unit) =
    doInit(TR(), init)
}

class TR: Tag("tr") {
  fun td(init: TD.() -> Unit) =
    doInit(TD(), init)
}

class TD: Tag("td")

fun createTable() =
  table {
    tr {
      td {}
    }
  }

// <table><tr><td></td></tr></table>
println(createTable())

// ------------------------------

// the `invoke` convention allows you to call objects of custom types as functions
// a class for which the `invoke` method with an `operator` modifier is defined can be called as a function

class Greeter(val greeting: String) {
  operator fun invoke(name: String) {
    println("$greeting, $name")
  }
}

// TODO is this a currying function ??? https://en.proft.me/2018/08/12/currying-function-kotlin

val hello = Greeter("Hello")
println(hello("World"))

println(Greeter("Hello")("World"))

// ------------------------------

// Lambdas, unless inlined, are compiled into classes that implement functional interfaces (Function1 and so on),
// and those interfaces define the invoke method with the corresponding number of parameters
// When you invoke a lambda as a function, the operation is translated into a call of the invoke method, thanks to the convention

// ------------------------------

// example of https://github.com/gradle/kotlin-dsl-samples

class DependencyHandler {
  // defines a regular command API
  fun compile(coordinate: String) =
    println("Added dependency on coordinate")

  // defines "invoke" to support the DSL API
  operator fun invoke(body: DependencyHandler.() -> Unit) =
    // "this" becomes a receiver of the body function: this.body()
    this.body()
}

val dependencies = DependencyHandler()
dependencies.compile("org.jetbrains.kotlin:kotlin-stdlib:1.0.0")

// - invoking "dependencies" as a function and passing a lambda as an argument
// - the type of the lambda's parameter is a function type with a receiver
// - the receiver type is the same DependencyHandler type
// - the invoke method calls the lambda
dependencies {
  compile("org.jetbrains.kotlin:kotlin-reflect:1.0.0")
  compile("org.jetbrains.kotlin:kotlin-reflect:1.0.0")
}
// equivalent to
dependencies.invoke({
  this.compile("org.jetbrains.kotlin:kotlin-reflect:123")
})

// ------------------------------

// example of https://github.com/kotest/kotest

interface Matcher<T> {
  fun test(value: T): Boolean
}

infix fun <T> T.should(matcher: Matcher<T>) =
  matcher.test(this)

class startsWith(val prefix: String): Matcher<String> {
  override fun test(value: String): Boolean =
    when {
      value.startsWith(prefix) -> true
      else -> false
    }
}

"myString" should startsWith("my")

// ------------------------------

object starts

infix fun String.verify(x: starts): StartWrapper = StartWrapper(this)

class StartWrapper(val value: String) {
  infix fun with(prefix: String): Boolean =
    when {
      value.startsWith(prefix) -> true
      else -> false
    }
}

"myString" verify starts with("my")

// ------------------------------

// example of https://github.com/yole/kxdate

// "this" refers to the value of the numeric constant
val Int.days: java.time.Period get() =
  java.time.Period.ofDays(this)

// invokes LocalDate.minus using operator syntax
val java.time.Period.ago: java.time.LocalDate get() =
  java.time.LocalDate.now() - this

// invokes LocalDate.plus using operator syntax
val java.time.Period.fromNow: java.time.LocalDate get() =
  java.time.LocalDate.now() + this

println(1.days.ago)
println(1.days.fromNow)

// ------------------------------

// member extensions: declaring extension functions and extension properties in a class

// https://github.com/jetbrains/exposed
// https://github.com/Kotlin/anko
