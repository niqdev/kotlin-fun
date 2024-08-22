// if your class defines a special method named `plus`, then, by convention, you can use the `+` operator on instances of this class.
// in Kotlin we refer to this technique as "conventions"

// Kotlin has a limited set of operators that you can overload, and each one corresponds to the name of the function you need to define in your class
// Kotlin operators don't automatically support "commutativity" (the ability to swap the left and right sides of an operator)

// overloading of binary operators (operators that are applied to two values)
data class Point(
  val x: Int,
  val y: Int,
) {
  // all functions used to overload operators need to be marked with `operator` keyword
  operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
}

Point(10, 20) + Point(30, 40)

// alternative using extension function syntax
operator fun Point.times(scale: Double): Point = Point((x * scale).toInt(), (y * scale).toInt())

// ------------------------------

// Kotlin doesn't define any bitwise operators for standard number types
// the full list of functions provided by Kotlin for performing bitwise operations
// shl - Signed shift left
// shr - Signed shift right
// ushr - Unsigned shift right
// and — Bitwise and
// or - Bitwise or
// xor -Bitwise xor
// inv - Bitwise inversion

0x0F and 0xF0
0x0F or 0xF0
0x1 shl 4

// ------------------------------

// Operators such as `+=`, `-=`, and so on are called "compound assignment operators"
// >>> mutability

// ------------------------------

// overloading unary operators: +a, -a, !a, ++a, a++, --a, a--
operator fun Point.unaryMinus(): Point = Point(-x, -y)

// ------------------------------

// the `==` operator in Kotlin is translated into a call of the equals method

// identity equals operator `===` checks whether the parameter to equals is the same object as the one on which equals is called
// the identity equals operator does exactly the same thing as the `==` operator in Java: it checks that both of its arguments reference the same object (or have the same value, if they have a primitive type)
// the === operator can't be overloaded
// equals can't be implemented as an extension, because the implementation inherited from the Any class would always take precedence over the extension

// ------------------------------

class Person(
  val firstName: String,
  val lastName: String,
) : Comparable<Person> {
  override fun compareTo(other: Person): Int = compareValuesBy(this, other, Person::lastName, Person::firstName)
}

Person("Alice", "Smith") < Person("Bob", "Johnson")

// ------------------------------

// To `get` or `set` an element by index, you use the syntax a[b] (called the index operator)
// The `in` operator can be used to check whether an element is in a collection or range and also to iterate over a collection

// p[0] to access the X coordinate and p[1] to access the Y coordinate
operator fun Point.get(index: Int): Int =
  when (index) {
    0 -> x
    1 -> y
    else -> TODO()
  }

val point = Point(10, 20)
point[0]

// ------------------------------

data class Rectangle(
  val upperLeft: Point,
  val lowerRight: Point,
)

// use the `until` standard library function to build an open range and then use the `in` operator on a range to check that a point belongs to it
// CLOSED-RANGE: 10..20 (20 included)
// OPEN-RANGE: 10 until 20 (20 excluded)
operator fun Rectangle.contains(p: Point): Boolean =
  p.x in upperLeft.x until lowerRight.x &&
    p.y in upperLeft.y until lowerRight.y

// a in c -> c.contains(a)
val rectangle = Rectangle(Point(10, 20), Point(50, 50))
Point(20, 30) in rectangle
Point(5, 5) in rectangle

// ------------------------------

// `..` is equivalent to `rangeTo` which uses `Comparable`
val now = java.time.LocalDate.now()
// now.rangeTo(now.plusDays(10))
val tenDays = now..now.plusDays(10)
now.plusWeeks(1) in tenDays

// the rangeTo operator has lower priority than arithmetic operators
// you have to surround a range expression with parentheses to call a method on it
(0..10).forEach { print(it) }

// ------------------------------

// `for` loops in Kotlin use the same in operator as range checks. But its meaning is different in this context: it's used to perform iteration
// `for (x in list) { ... }` will be translated into a call of `list.iterator()`, on which the `hasNext` and `next` methods are then repeatedly called

// ------------------------------

// "destructuring declarations" allows you to unpack a single composite value and use it to initialize several separate variables
val myPoint = Point(10, 20)
val (myX, myY) = myPoint

// ror a data class, the compiler generates a componentN function for every property declared in the primary constructor
myPoint.component1()
myPoint.component2()

val map = mapOf("Oracle" to "Java", "JetBrains" to "Kotlin")
for ((key, value) in map) {
  println("$key -> $value")
}

// ------------------------------

// delegated properties
// For example, properties can store their values in database tables, in a browser session, in a map, and so on
// "delegation": a design pattern where an object, instead of performing a task, delegates that task to another helper object. The helper object is called a delegate
/*
class Foo {
  var p: Type by Delegate()
}
*/

// ------------------------------

// See 7.5

// "Lazy initialization" is a common pattern that entails creating part of an object on demand, when it's accessed for the first time and only once

// The lazy function returns an object that has a method called getValue with the proper signature
// use it together with the `by` keyword to create a delegated property
// The argument of `lazy` is a lambda that it calls to initialize the value
// The `lazy` function is thread-safe by default
// The object to the right of by is called the `delegate`.
// Kotlin automatically stores the delegate in a hidden property and calls `getValue` and `setValue` on the delegate when you access or modify the main property
/*
class Person0(val name: String) {
  val emails by lazy { loadEmails(this) }
}

class ObservableProperty(
  var propValue: Int, val changeSupport: java.beans.PropertyChangeSupport
){
  operator fun getValue(p: Person, prop: kotlin.reflect.KProperty<*>): Int = propValue
  operator fun setValue(p: Person, prop: kotlin.reflect.KProperty<*>, newValue: Int) {
    val oldValue = propValue
    propValue = newValue
    changeSupport.firePropertyChange(prop.name, oldValue, newValue)
  }
}
class Person1(
  val name: String, age: Int, salary: Int
) : PropertyChangeAware() {
  var age: Int by ObservableProperty(age, changeSupport)
  var salary: Int by ObservableProperty(salary, changeSupport)
}
*/

// Another common pattern where delegated properties come into play is objects that have a dynamically defined set of attributes associated with them. Such objects are sometimes called `expando objects`
