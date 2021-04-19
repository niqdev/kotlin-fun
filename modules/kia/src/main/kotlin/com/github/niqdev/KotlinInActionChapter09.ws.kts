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
