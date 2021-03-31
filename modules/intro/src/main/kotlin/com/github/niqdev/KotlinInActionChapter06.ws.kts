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
