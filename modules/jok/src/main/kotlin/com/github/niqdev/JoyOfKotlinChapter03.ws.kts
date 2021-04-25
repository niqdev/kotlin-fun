// in functional programming everything is a function, there is no difference between data and functions: values are functions, functions are functions, and effects are functions
// instead of applying effects, functional programmers use functions returning data representing the intended effect in a non-evaluated form

// a function is primarily a mathematical concept
// it represents a relationship between a source set (called the function `domain`) and a target set (called the function `codomain`)
// the domain and the codomain need not be distinct
// to be a function, a relationship must fulfill one condition: all elements of the domain must have one and only one corresponding element in the codomain
// the set of elements of the codomain that have a corresponding element in the domain is called the `image` of the function

// a function may or may not have an inverse function

// to be a function, a relationship has two requirements
// - it must be defined for all elements of the domain
// - no element of the domain can have a relationship with more than one element of the codomain
// a relationship that isn't defined for all elements of the domain but that fulfills the rest of the requirement is often called a `partial function`
// a nonpartial function is sometimes called a `total function`

// ------------------------------

// a function is a relationship between a source set and a target set:
// a function can't have several arguments
// the product of two sets is itself a set
// a pair is a tuple of two elements: a concept that's used to represent combinations of several elements
// a function of one tuple and not a function of two arguments

// f(x)(y) = x + y
// f(x) = g
// g(y) = x + y
// when applying g, x is no longer a variable but a constant
// f(3)(5) = g(5) = 3 + 5 = 8
// the codomain of f is a set of functions instead of a set of numbers: the result of applying f to an integer is a function
// the result of applying this resulting function to an integer is an integer
// f(x)(y) is the curried form of the function f(x, y)

// ------------------------------

fun double0(x: Int): Int = x * 2

// value function
val double1: (Int) -> Int = { x -> x * 2 }
val double2 = { x: Int -> x * 2 }
// single element (no tuple)
val double3: (Int) -> Int = { it * 2 }

// functions of tuples: single argument
val add: (Int, Int) -> Int = { x, y -> x + y }

// ------------------------------

// function reference
val doubleRef0: kotlin.reflect.KProperty0<(Int) -> Int> = ::double1

class MyClass {
  fun ref0(): Int = 8
  companion object {
    fun ref1(): Int = 42
  }
}
val myClass = MyClass()
val myRef0: () -> Int = myClass::ref0
val myRef1: kotlin.reflect.KFunction0<Int> = myClass::ref0
val myRef2: () -> Int = (MyClass)::ref1
val myRef3: kotlin.reflect.KFunction0<Int> = (MyClass)::ref1
val myRef4: (MyClass) -> Int = MyClass::ref0
val myRef5: kotlin.reflect.KFunction1<MyClass, Int> = MyClass::ref0

// ---------- 3.1 ----------

fun compose0(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int = { x -> f(g(x)) }
fun compose1(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int = { f(g(it)) }

val square: (Int) -> Int = { it * it }
fun triple(value: Int): Int = value * 3
val squareOfTriple: (Int) -> Int = compose1(square, ::triple)
println(squareOfTriple(2))

// ---------- 3.2 ----------

fun <I, O, T> compose2(f: (T) -> O, g: (I) -> T): (I) -> O = { f(g(it)) }

fun <T> ((T) -> T).compose3(g: (T) -> T): (T) -> T = { this(g(it)) }

square.compose3(::triple)

// ---------- 3.3 ----------

// integer binary operator

//typealias IntBinOp = (Int) -> (Int) -> Int
val myAdd: (Int) -> (Int) -> Int = { a -> { b -> a + b } }
myAdd(1)(2)

// ---------- 3.4 ----------

// a function taking functions as its arguments and returning functions, is called a higher-order function (HOF)

val compose4: ((Int) -> Int) -> ((Int) -> Int) -> (Int) -> Int = { f -> { g -> { value -> f(g(value)) } }}
compose4(square)(::triple)(2)

// ---------- 3.5 ----------

// >>> you can't define polymorphic properties

fun <I, O, T> higherCompose(): ((T) -> O) -> ((I) -> T) -> (I) -> O = { f -> { g -> { value -> f(g(value)) } }}
// Not enough information to infer parameter: specify types <Int, Int, Int>
higherCompose<Int, Int, Int>()(square)(::triple)(2)

// ---------- 3.6 ----------

fun <I, O, T> higherAndThen(): ((I) -> T) -> ((T) -> O) -> (I) -> O = { f -> { g -> { value -> g(f(value)) } }}
higherAndThen<Int, Int, Int>()(square)(::triple)(2)

// ------------------------------

// anonymous functions
// Don't worry about the creation of anonymous functions. Kotlin won't always create new objects each time the function is called. Instantiating such objects is cheap
// focus on clarity and maintainability of your code
// If you're concerned with performance and reusability, you should use function references as often as possible

val cos0 = higherCompose<Double, Double, Double>()({ x: Double -> Math.PI / 2 - x })(Math::sin)
val cos1 = higherCompose<Double, Double, Double>()() { x: Double -> Math.PI / 2 - x }(Math::sin)

val cosValue: Double = cos1(2.0)

// ------------------------------

fun cos2(arg: Double): Double {

  // local functions
  fun f(x: Double): Double = Math.PI / 2 - x
  fun sin(x: Double): Double = Math.sin(x)
  return higherCompose<Double, Double, Double>()(::f)(::sin)(arg)
}

// Kotlin lambdas can access mutable variables of the enclosing scope

// ------------------------------

// currying and partial application are closely related
// currying consists in replacing a function of a tuple with a new function that you can partially apply, one argument after another
// with a function of a tuple, all arguments are evaluated before the function is applied

// ---------- 3.7 ----------

fun <I, O, T> applyCurried(input: I, f: (I) -> (T) -> O): (T) -> O = f(input)
