// `use-site` annotation target declaration e.g. @get:Rule, @file:JvmName
// see 10.1.2

// @Volatile
// @Strictfp
// @JvmName changes the name of a Java method or field generated from a Kotlin declaration
// @JvmStatic can be applied to methods of an object declaration or a companion object to expose them as static Java methods
// @JvmOverloads instructs the Kotlin compiler to generate overloads for a function that has default parameter values
// @JvmField can be applied to a property to expose that property as a public Java field with no getters or setters

// Serialization is a process of converting an object to a binary or text representation that can be then stored or sent over the network.
// The reverse process, deserialization, converts such a representation back to an object.
// One of the most common formats used for serialization is JSON
// runtime ;-( ... https://github.com/FasterXML/jackson | https://github.com/google/gson | https://github.com/yole/jkid

// Because annotation classes are only used to define the structure of metadata associated with declarations and expressions,
// they can't contain any code. Therefore, the compiler prohibits specifying a body for an annotation class
annotation class JsonExclude

// the annotations that can be applied to annotation classes are called meta-annotations
@Target(AnnotationTarget.PROPERTY)
// the parameters are declared in the primary constructor of the class
annotation class JsonName(
  val name: String,
)

// ------------------------------

// to declare your own meta-annotation, use ANNOTATION_CLASS as its target
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class BindingAnnotation

// in Java the meta-annotation @Retention is used to specify whether the annotation you declare will be stored in the .class file and whether it will be accessible at runtime through reflection
// in Kotlin by default annotations have RUNTIME retention (no need to specify it)

// ------------------------------

// KClass type is Kotlin's counterpart to Java's java.lang.Class type
// allows to control the deserialization of properties that have an interface type
annotation class DeserializeInterface(
  val targetClass: kotlin.reflect.KClass<out Any>,
)

interface Company {
  val name: String
}

data class CompanyImpl(
  override val name: String,
) : Company

data class Person(
  val name: String,
  // the type of the annotation argument CompanyImpl::class (KClass<CompanyImpl>) is a subtype of the annotation parameter type (KClass<out Any>)
  // if you wrote KClass<Any> without the out modifier, you wouldn't be able to pass CompanyImpl::class as an argument: the only allowed argument would be Any::class.
  // the out keyword specifies that you're allowed to refer to classes that extend Any, not just to Any itself
  @DeserializeInterface(CompanyImpl::class) val company: Company,
)

// ------------------------------

interface ValueSerializer<T> {
  fun toJsonValue(value: T): Any?

  fun fromJsonValue(jsonValue: Any?): T
}

annotation class CustomSerializer(
  // you know nothing about the types of properties with which this annotation will be used
  val serializerClass: kotlin.reflect.KClass<out ValueSerializer<*>>,
)

// ------------------------------

// Reflection is a way to access properties and methods of objects dynamically at runtime, without knowing in advance what those properties are

// java.lang.reflect
// kotlin.reflect

class Example(
  string: String,
)
val example = Example("hello")

// KClass is the counterpart of java.lang.Class
// https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class
println(Example::class)

// to get the class of an object at runtime
val kClass = example.javaClass.kotlin
println(kClass)

kClass.members.forEach { println(it.name) }

// ------------------------------

fun foo(x: Int) = println(x)
// these function types are synthetic compiler-generated types, and you won't find their declarations in the kotlin.reflect package
val kFunction: kotlin.reflect.KFunction<Unit> = ::foo
// runtime invocation
kFunction.call(42)

// ------------------------------

var counter = 0
// member property
val kProperty: kotlin.reflect.KProperty0<Int> = ::counter
kProperty.get()

// ------------------------------

// three main stages: a lexical analyzer, usually referred to as a lexer; a syntax analyzer, or parser; and the deserialization component itself

// The lexical analysis splits an input string consisting of characters into a list of tokens
// There are two kinds of tokens: character tokens, which represent characters with special meanings in the JSON syntax (comma, colon, braces, and brackets); and value tokens, which correspond to string, number, Boolean, and null constants

// The parser is generally responsible for converting a plain list of tokens into a structured representation

// The deserializer then provides an implementation for JsonObject that gradually builds a new instance of the corresponding type
