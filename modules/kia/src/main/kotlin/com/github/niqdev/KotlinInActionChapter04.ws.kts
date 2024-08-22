// interfaces can contain property declarations

interface Clickable {
  fun click()

  fun showOff() = println("I'm clickable!")
}

interface Focusable {
  fun showOff() = println("I'm focusable!")
}

// Kotlin uses the colon after the class name to replace both the `extends` and `implements` keywords used in Java
// must implement method
class Button :
  Clickable,
  Focusable {
  // unlike Java, using the override modifier is mandatory in Kotlin
  override fun click() = println("I was clicked")

  // >>> DIAMOND problem: if 2 interfaces have a method with the same name
  // you get a compiler error and must provide your own implementation
  override fun showOff() {
    super<Clickable>.showOff()
    super<Focusable>.showOff()
  }
}
Button().click()

// ------------------------------

// Java best practice: all classes and methods that aren't specifically intended to be overridden in subclasses ought to be explicitly marked as final
// Kotlin classes and methods declarations are final and public by default
// If you want to allow the creation of subclasses of a class, you need to mark the class with the `open` modifier
// and you need to add the `open` modifier to every property or method that can be overridden

// This class is open: others can inherit from it
open class RichButton : Clickable {
  // This function is final: you can't override it in a subclass
  fun disable() {}

  // This function is open: you may override it in a subclass
  open fun animate() {}

  // This function overrides an open function and is open as well
  override fun click() {}

  // to forbid the subclasses of your class from overriding your implementation, you can explicitly mark the overriding member as final
  // `final` isn't redundant here because `override` without `final` implies being open
  // final override fun click() {}
}

// In Kotlin, as in Java, you may declare a class abstract, and such classes can't be instantiated.
// An abstract class usually contains abstract members that don't have implementations and must be overridden in subclasses.
// Abstract members are always open, so you don't need to use an explicit open modifier

// This class is abstract: you can't create an instance of it.
abstract class Animated {
  // This function is abstract: it doesn't have an implementation and must be overridden in subclasses
  abstract fun animate()

  //  Non-abstract functions in abstract classes aren't open by default but can be marked as open
  open fun stopAnimating() {}

  fun animateTwice() {}
}

// ------------------------------

// - visibility modifiers: public, protected, and private
// - the default visibility is `public`
// - the default visibility in Java, package-private, isn't present in Kotlin.
// - Kotlin uses packages only as a way of organizing code in namespaces; it doesn't use them for visibility control
// - Kotlin offers a new visibility modifier, `internal`, which means visible inside a module
// - A module is a set of Kotlin files compiled together
// - Kotlin allows the use of private visibility for top-level declarations, including classes, functions, and properties. Such declarations are visible only in the file where they are declared
// - protected member is only visible in the class and its subclasses
// - extension functions of a class don't get access to its private or protected members
// - an outer class doesn't see private members of its inner (or nested) classes: they don't contain an implicit reference to their outer class

class Outer {
  class Nested

  inner class Inner {
    //  to reference an instance of an outer
    val outer: Outer = this@Outer
  }
}

// ------------------------------

// All the direct subclasses must be nested in the superclass or in the same file
// If you handle all subclasses of a sealed class in a when expression, you don't need to provide the default branch
// Under the hood, the Expr class has a private constructor, which can be called only inside the class.
// You can't declare a sealed interface
sealed class Expr {
  // the `()` means invoke the default constructor
  class Num(
    val value: Int,
  ) : Expr()

  class Sum(
    val left: Expr,
    val right: Expr,
  ) : Expr()
}

// ------------------------------

// All the declarations of the User class are equivalent

// distinction between a `primary` constructor (which is usually the main, concise way to initialize a class and is declared outside of the class body)
// and a `secondary` constructor (which is declared in the class body).
// It also allows you to put additional initialization logic in `initializer blocks`

// primary constructor with one parameter
// The underscore in the constructor parameter _name serves to distinguish the name of the property from the name of the constructor parameter
class UserVerbose0 constructor(
  _name: String,
) {
  val name: String

  // initializer block
  // contains initialization code that's executed when the class is created,
  // and are intended to be used together with primary constructors
  // If you want to, you can declare several initializer blocks in one class
  init {
    // alternative use `this.name` if you don't like _name
    name = _name
  }
}

// Primary constructor with one parameter
class UserVerbose1(
  _name: String,
) {
  // The property is initialized with the parameter
  val name = _name
}

// primary constructor
// It serves two purposes: specifying constructor parameters and defining properties that are initialized by those parameters
// `val` means the corresponding property is generated for the constructor parameter
// Provides a default value for the constructor parameter
// create an instance of a class, you call the constructor directly, without the new keyword
// If all the constructor parameters have default values, the compiler generates an additional constructor without parameters that uses all the default values
class User(
  val name: String,
  val isSubscribed: Boolean = true,
)

// If your class has a superclass, the primary constructor also needs to initialize the superclass
open class User1(
  val name: String,
)

class TwitterUser1(
  val nickname: String,
) : User1(nickname)

// private constructor: ensure that your class can't be instantiated
class Secretive private constructor()

// ------------------------------

// multiple constructors are usually replaced with default parameters
// mainly fo Java interoperability
// This class doesn't declare a primary constructor (as you can tell because there are no parentheses after the class name in the class header)
open class View {
  constructor(ctx: String) {}
  constructor(ctx: String, attr: String) {}
}

class MyButton : View {
  // invokes View constructor
  constructor(ctx: String) : super(ctx)

  // invokes MyButton constructor
  constructor(ctx: String, attr: String) : this(ctx)
}

// next chapters
// lateinit
// @JvmField
// const

// ------------------------------

// Universal object methods: toString, equals, and hashCode
data class ClientData(
  val name: String,
  val postalCode: Int,
)

// equivalent to
class Client(
  val name: String,
  val postalCode: Int,
) {
  override fun toString() = "Client(name=$name, postalCode=$postalCode)"

  // If applied to primitive types, Java's == compares values, whereas == on reference types compares references
  // in Java, there's the well-known practice of always calling equals, and there’s the well-known problem of forgetting to do so
  // In Kotlin, the == operator is the default way to compare two objects: it compares their values by calling equals under the hood
  // For reference comparison, you can use the === operator
  override fun equals(other: Any?): Boolean {
    if (other == null || other !is Client) {
      return false
    }
    return name == other.name &&
      postalCode == other.postalCode
  }

  // The hashCode method should be always overridden together with equals
  // e.g. hashSetOf + .contains will return wrong value otherwise
  override fun hashCode(): Int = name.hashCode() * 31 + postalCode
}

// ------------------------------

val client1 = ClientData(name = "myName", postalCode = 123)
val client2 = client1.copy(name = "myNewName")
println(client1)
println(client2)

// ------------------------------

// Decorator pattern: `by` keyword
class DelegatingCollection<T>(
  innerList: Collection<T> = ArrayList<T>(),
) : Collection<T> by innerList

// ------------------------------

// - "object" declaration is a way to define a singleton
// - "companion objects" can contain factory methods and other methods that are related to this class
// but don't require a class instance to be called. Their members can be accessed via class name
// - "object expression" is used instead of Java's anonymous inner class

// SINGLETON
// the object declaration combines a "class declaration" and a declaration of a "single instance" of that class
// The only things that aren't allowed are constructors (either primary or secondary)
// in Java you access it with Payroll.INSTANCE
object Payroll {
  val allEmployees = arrayListOf<String>()

  fun calculateSalary() {
    for (person in allEmployees) {
      println(person)
    }
  }
}

// ------------------------------

// COMPANION OBJECT
class Example1 private constructor(
  val name: String,
) {
  private val bar = 1

  // only one companion object is allowed per class
  companion object {
    // alternative to secondary constructors: factories
    fun instance() = Example1("hello")
  }
}

class Example2(
  val name: String,
) {
  companion object Foo {
    fun bar() = println(this.javaClass)
  }
}
println("${Example1.instance()} ${Example2.Foo.bar()}")

// a companion object can implement interfaces
// in Java: Person.Companion.fromJSON
interface JSONFactory<T> {
  fun fromJSON(jsonText: String): T
}

class Example3(
  val name: String,
) {
  companion object : JSONFactory<Example3> {
    override fun fromJSON(jsonText: String): Example3 {
      TODO("Not yet implemented")
    }
  }
}

// See @JvmStatic @JvmField for Java interop

// COMPANION OBJECT extension
class Example4(
  val name: String,
) {
  // you have to declare a companion object in your class, even an empty one, in order to be able to define extensions to it
  companion object
}

fun Example4.Companion.foo(bar: String): Example4 = Example4(bar)

println(Example4.foo("bar").name)

// ------------------------------

// declaring anonymous objects
// Unlike object declarations, anonymous objects aren’t singletons. Every time an object expression is executed, a new instance of the object is created
// Object expressions are mostly useful when you need to override multiple methods in your anonymous object
val myExample =
  object : JSONFactory<Example4> {
    override fun fromJSON(jsonText: String): Example4 {
      TODO("Not yet implemented")
    }
  }
