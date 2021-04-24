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
