// `Reified type` parameters allow you to refer at runtime to the specific types used as type arguments in an inline function call.
// For normal classes or functions, this isn't possible, because type arguments are erased at runtime.

// `Declaration-site variance` lets you specify whether a generic type with a type argument is a subtype or a supertype of another generic type with the same base type and a different type argument.
// `Use-site variance` achieves the same goal for a specific use of a generic type and therefore accomplishes the same task as Java's wildcards

// ------------------------------

// `Generics` allow you to define types that have `type parameters`
// When an instance of such a type is created, type parameters are substituted with specific types called `type arguments`

interface MyList<T> {
  operator fun get(index: Int): T
}

// ------------------------------

// Type parameter constraints let you restrict the types that can be used as type arguments for a class or function
// `:` single upper bound constraint
interface MyComparable<T: MyComparable<T>> {
  fun compareTo(other: T): Int
}

// specify multiple constraints on a type parameter
fun <T> ensureTrailingPeriod(seq: T): String where T : CharSequence, T : Appendable = TODO()

// ------------------------------

// If you declare a generic class or function, any type arguments, including nullable ones, can be substituted for its type parameters
// a type parameter with no upper bound specified will have the upper bound of `Any?`
class Processor0<T> {
  fun process(value: T) {
    value?.hashCode()
  }
}
// verbose
val nullableStringProcessor = Processor0<String?>()
nullableStringProcessor.process(null)

// If you want to guarantee that a non-null type will always be substituted for a type parameter, you can achieve this by specifying a constraint
// The <T : Any> constraint ensures that the T type will always be a non-nullable type
class Processor1<T : Any> {
  fun process(value: T) {
    value.hashCode()
  }
}

// ------------------------------

// generics on the JVM are normally implemented through type erasure,
// meaning the type arguments of an instance of a generic class aren't preserved at runtime
// You can declare an `inline` function so that its type arguments aren't erased/reified

// usually it's safe because the compiler knows the type arguments and ensures that only elements of the correct type are stored in each list
// although you can deceive the compiler through type casts

// erasing generic type information has its benefits: the overall amount of memory used by your application is smaller, because less type information needs to be saved in memory

// it's not possible to use types with type arguments in is checks
// if (value is List<String>) { ... } <<< does NOT compile

// star projection syntax
// if (values is List<*>) { ... } <<< does compile

// `as` and `as?` casts won't fail if the class has the correct base type and a wrong type argument,
// because the type argument isn't known at runtime when the cast is performed.
// Because of that, the compiler will emit an `unchecked cast` warning on such a cast

// ------------------------------

// Kotlin generics are erased at runtime, which means if you have an instance of a generic class, you can't find out the type arguments used when the instance was created.
// The same holds for type arguments of a function.
// Type parameters of inline functions can be reified, which means you can refer to actual type arguments at runtime.

// if you mark a function with the `inline` keyword, the compiler will replace every call to the function with the actual code implementing the function
// - making the function inline may improve performance if this function uses lambdas as arguments: the lambda code can be inlined as well, so no anonymous class will be created
// - if you declare the previous a function as inline and mark the type parameter as reified, you can check value to see whether it's an instance of T

inline fun <reified T> isA(value: Any) = value is T
println(isA<String>("abc"))
println(isA<String>(123))

// the type argument is known at runtime
val items = listOf("one", 2, "three")
println(items.filterIsInstance<String>())

// the compiler inserts the bytecode implementing the inline function into every place where it's called.
// Every time you call the function with a reified type parameter, the compiler knows the exact type used as the type argument in that particular call.
// Therefore, the compiler can generate the bytecode that references the specific class used as a type argument

// example
inline fun <reified T> loadService() =
  java.util.ServiceLoader.load(T::class.java)

// >>> see 9.2.4 for restrictions

// ------------------------------

// the concept of `variance` describes how types with the same base type and different type arguments relate to each other

// A type B is a `subtype` of a type A if you can use the value of the type B whenever a value of the type A is required
// Int is a subtype of Number
// a type is considered a subtype of itself
// supertype is the opposite of subtype. If A is a `subtype` of B, then B is a `supertype` of A

// the compiler performs this check every time when you assign a value to a variable or pass an argument to a function
// - storing a value in a variable is allowed only when the value type is a subtype of the variable type
// - passing an expression to a function is allowed only when the type of the expression is a subtype of the function parameter type

// in simple cases, `subtype` means essentially the same thing as `subclass`
// nullable types provide an example of when subtype isn't the same as subclass
// a non-null type is a subtype of its nullable version, but they both correspond to one class
// i.e. String is a subtype of String?

// A generic class — for instance, MutableList — is called `invariant` on the type parameter if, for any two different types A and B,
// MutableList<A> isn't a subtype or a supertype of MutableList<B>
// The List interface in Kotlin represents a read-only collection. If A is a subtype of B, then List<A> is a subtype of List<B>. Such classes or interfaces are called `covariant`

// ------------------------------

// covariance: preserved subtyping relation

// e.g. List<T> is covariant (read-only out position)

// Producer<A> is a subtype of Producer<B> if A is a subtype of B
// covariant on T
interface Producer<out T> {
  // "produces values of type T" output position
  fun produce(): T
}

// to guarantee type safety, it can be used only in so-called `out` positions, meaning the class can produce values of type T but not consume them

// ------------------------------

// contravariance: reversed subtyping relation

// Comparator<Any> is a subtype of Comparator<String>, where Any is a supertype of String
// a comparator of List<String> can accept a List<Any>
interface Consumer<in T> {
  fun consumer(t: T): Boolean
}

// a class that is contravariant on the type parameter is a generic class (let's consider Consumer<T> as an example) for which the following holds:
// Consumer<A> is a subtype of Consumer<B> if B is a subtype of A

// ------------------------------

// Cat is a subtype of Animal
// Producer<Cat> is a subtype of Producer<Animal> (covariant): an animalProducer can produce Cat
// Consumer<Animal> is a subtype of Consumer<Cat> (controvariant): a catConsumer can consume Animal

// contravariant on its argument and covariant on its return type
interface Function1<in I, out O> {
  fun invoke(i: /* IN consume */ I): /* OUT produce */ O
}

// ------------------------------

// declaration-site variance: specify variance modifiers on class declarations
// use-site variance: every time you use a type with a type parameter, you can also specify whether this type parameter can be replaced with its subtypes or supertypes

// `type projection`: "source" isn't a regular MutableList, but a `projected` (restricted) one
fun <T> copyData(source: MutableList<out T>,
                 destination: MutableList<in T>) {
  for (item in source) {
    destination.add(item)
  }
}

// ------------------------------

// star projection: using * instead of a type argument
// star-projection syntax indicate that you have no information about a generic argument

// MutableList<*> is projected to (acts as) MutableList<out Any?>
// when you know nothing about the type of the element, it's safe to get elements of Any? type, but it's not safe to put elements into the list
