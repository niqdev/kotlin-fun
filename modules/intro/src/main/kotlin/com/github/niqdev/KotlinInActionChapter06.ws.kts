// Nullability is a feature of the Kotlin type system that helps you avoid NullPointerException errors
// convert these problems from runtime errors into compile-time errors.
// By supporting nullability as part of the type system, the compiler can detect many possible errors during compilation and reduce the possibility of having exceptions thrown at runtime

fun strLen(s: String) = s.length

strLen("aaa")
// Calling strLen with an argument that may be null isn't allowed and will be flagged as error at compile time
//strLen(null)

// put a question mark after any type, to indicate that the variables of this type can store `null`
// a type without a question mark denotes that variables of this type can't store null references
// Once you have a value of a nullable type, the set of operations you can perform on it is restricted
// >>> it doesn't compile `s.length`
fun unsafeStrLen0(s: String?): Int = if (s != null) s.length else 0
unsafeStrLen0(null)

// Objects of nullable or non-null types at runtime are the same
// All checks are performed at compilation time. That means there's no runtime overhead for working with nullable types in Kotlin

// ------------------------------

// safe-call operator: `?.`

fun unsafeStrLen1(s: String?): Int? = s?.length
unsafeStrLen1("111")
// return `null`
unsafeStrLen1(null)

// ------------------------------

// Elvis operator or the null-coalescing operator: `?:`

class Address(val streetAddress: String, val zipCode: Int, val city: String, val country: String)
class Company(val name: String, val address: Address?)
class Person(val name: String, val company: Company?)

fun Person.countryName(): String =
  this.company?.address?.country ?: "unknown"

fun printShippingLabel(person: Person) {
  // throw ;-(
  val address = person.company?.address ?: throw IllegalArgumentException("No address")

  with (address) {
    println(streetAddress)
    println("$zipCode $city, $country")
  }
}

// ------------------------------

// safe-cast operator: as?
// tries to cast a value to the specified type and returns null if the value doesn't have the proper type

// not-null assertion: !!
// converts any value to a non-null type, for null values, an exception (KotlinNullPointerException) is thrown
// When you check for null in one function and use the value in another function, the compiler can't recognize that the use is safe.
// If you're certain the check is always performed in another function, you may not want to duplicate it before using the value; then you can use a not-null assertion instead

// ------------------------------

fun sendEmail(email: String?) { println("send $email") }
val email: String? = "aaa@bbb.com"
email.let { e -> sendEmail(e) }
email?.let { sendEmail(it) }

// ------------------------------

// declare a property as late-initialized
// A common use case for `lateinit` properties is dependency injection

// Note that a late-initialized property is always a `var`, because you need to be able to change its value outside of the constructor,
// and `val` properties are compiled into final fields that must be initialized in the constructor.
// But you no longer need to initialize it in a constructor, even though the property has a non-null type
class MyTest {
  // without `lateinit` it won't compile unless you do it in the CONSTRUCTOR
  // this is usually used in java-junit-mocking style
  lateinit var myLateInitVar: String

  // example NON-CONSTRUCTOR
  fun setup() {
    myLateInitVar = "aaa"
  }

  fun myJunitTest() {
    println("$myLateInitVar")
  }
}

// ------------------------------

// call an extension function that was declared for a nullable receiver without safe access
// in an extension function for a nullable type, `this` can be `null`
"".isNullOrEmpty()

// ------------------------------

// By default, all type parameters of functions and classes in Kotlin are nullable

fun <T> printHashCode0(t: T) {
  // the inferred type for the type parameter `T` is a nullable type `Any?`
  // Therefore, the parameter `t` is allowed to hold null, even without a question mark after `T`
  println(t?.hashCode())
}
printHashCode0(null)

// To make the type parameter non-null, you need to specify a non-null upper bound for it. That will reject a nullable value as an argument.
fun <T: Any> printHashCode1(t: T) {
  println(t.hashCode())
}
// DOESN'T COMPILE
//printHashCode1(null)

// ------------------------------

// java interop
// - @Nullable String in Java is seen as String? by Kotlin
// - @NotNull String in Java is just String by Kotlin

// A platform type is essentially a type for which Kotlin doesn’t have nullability information
// as in Java, you have full responsibility for the operations you perform with that type. The compiler will allow all operations

// ------------------------------

// Kotlin doesn't differentiate primitive types and wrappers
val intList: List<Int> = listOf(1, 2, 3)

// Java makes a distinction between primitive types and reference types.
// A variable of a `primitive type` (such as int) holds its value directly.
// A variable of a `reference type` (such as String) holds a reference to the memory location containing the object

// ------------------------------

// `coerceIn` restrict the value to the specified range
fun showProgress(progress: Int): Int {
  val percentage = progress.coerceIn(0, 100)
  println("$progress | $percentage")
  return percentage
}

// - Integer types - Byte, Short, Int, Long
// - Floating-point number types - Float,Double
// - Character type - Char
// - Boolean type - Boolean
// the Kotlin types above are compiled under the hood to the corresponding Java primitive type, because the values of both types can't store the null reference

// Nullable types (e.g. Int?, Boolean?) in Kotlin can't be represented by Java primitive types, because null can only be stored in a variable of a Java reference type.
// That means whenever you use a nullable version of a primitive type in Kotlin, it's compiled to the corresponding wrapper type.

// ------------------------------

// - Literals of type Long use the L suffix: 123L
// - Literals of type Double use the standard representation of floating-point numbers: 0.12, 2.0, 1.2e10, 1.2e-10
// - Literals of type Float use the f or F suffix: 123.4f, .456F, 1e3f
// - Hexadecimal literals use the 0x or 0X prefix (such as 0xCAFEBABE or 0xbcdL)
// - Binary literals use the 0b or 0B prefix (such as 0b000000101)
// - valid character literals: '1', '\t' (the tab character), '\u0009' (the tab character represented using a Unicode escape sequence)
// toInt, toByte, toBoolean, and so on throws a NumberFormatException if the parsing fails

// ------------------------------

// the root types: `Any` and `Any?`

// Similar to how Object is the root of the class hierarchy in Java,
// the Any type is the supertype of all non-nullable types in Kotlin
// Any is a supertype of all types, including the primitive types such as Int
// Any is a non-nullable type
// If you need a variable that can hold any possible value including `null`, you must use the `Any?` type
// Under the hood, the Any type corresponds to `java.lang.Object` in the Java bytecode
// toString, equals, and hashCode are inherited from Any
val answer: Any = 42

// ------------------------------

// the unit/void type: `Unit`
// used in functional languages and it means "only one instance"

// Unit is a full-fledged type, and, unlike void, it can be used as a type argument
// you don't need to write an explicit return statement because return Unit is added implicitly by the compiler

// ------------------------------

// the `Nothing` type: "This function never returns"

// - throwing an exception
// - infinite loop

// ------------------------------

// collection of nullable value
val x: List<Int?> = listOf()
val y: List<Int> = x.filterNotNull()

// collection that holds a nullable list of nullable numbers
val z: List<Int?>? = listOf()

// ------------------------------

// kotlin.collections.Collection: read-only
// kotlin.collections.MutableCollection extends Collection

// you can use `defensive copy` with read-only collections
// - read-only collections aren't necessarily immutable
// - it's essential to understand that read-only collections aren't always thread-safe

/*

Collection Type | Read-only | Mutable
List | listOf | mutableListOf, arrayListOf
Set | setOf | mutableSetOf, hashSetOf, linkedSetOf, sortedSetOf
Map | mapOf | mutableMapOf, hashMapOf, linkedMapOf, sortedMapOf

*/

// ------------------------------

// types defined in Java code are seen as platform types in Kotlin

// ------------------------------

// >>> you should prefer using collections to arrays by default

val example1: Array<String> = arrayOf("aaa")
val letters = Array(26) { i -> ('a' + i).toString() }

val strings = listOf("a", "b", "c")
// one of the most common cases for creating an array in Kotlin code is when you need to call a Java method that takes an array, or a Kotlin function with a vararg parameter
// spread operator `*` is used to pass an array when vararg parameter is expected
println("%s/%s/%s".format(*strings.toTypedArray()))

// type arguments of array types always become object types
// an Array<Int> will become an array of boxed integers (its Java type will be java.lang.Integer[])
val ints: IntArray = intArrayOf(3, 2, 1)
