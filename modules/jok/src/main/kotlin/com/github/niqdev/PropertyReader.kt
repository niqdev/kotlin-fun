package com.github.niqdev

// TODO complete example
// https://github.com/pysaumont/fpinkotlin/blob/master/fpinkotlin-parent/fpinkotlin-commonproblems/src/main/kotlin/com/fpinkotlin/commonproblems/properties/example06/Properties.kt
class PropertyReader(
  configFileName: String,
) {
  private val properties: MyResult<java.util.Properties> =
    try {
      java.lang.invoke.MethodHandles
        .lookup()
        .lookupClass()
        .getResourceAsStream(configFileName)
        .use { inputStream ->
          when (inputStream) {
            null -> MyResult.failure("File $configFileName not found in classpath")
            else ->
              java.util.Properties().let {
                it.load(inputStream)
                MyResult(it)
              }
          }
        }
    } catch (e: java.io.IOException) {
      MyResult.failure("IOException reading classpath resource $configFileName")
    } catch (e: Exception) {
      MyResult.failure("Exception: ${e.message}reading classpath resource $configFileName")
    }

  fun readAsString(name: String): MyResult<String> =
    properties.flatMap<java.util.Properties, String> {
      MyResult
        .of {
          it.getProperty(name)
        }.mapFailure()("Property '$name' no found")
    }

  fun readAsInt(name: String): MyResult<Int> =
    readAsString(name).flatMap<String, Int> {
      try {
        MyResult(it.toInt())
      } catch (e: NumberFormatException) {
        MyResult.failure<Int>("Invalid value while parsing property '$name' to Int: $it")
      }
    }

  // Unlike Java, Kotlin allows accessing type parameters at runtime by using the keyword `reified`, so that it's not erased.
  // This possibility is only accessible in functions declared with `inline`,
  // meaning that the compiler is allowed to copy the code of the function at the call site, instead of referencing the original code.
  // This increases the size of the compiled code.
  inline fun <reified T : Enum<T>> readAsEnum(
    name: String,
    enumClass: Class<T>,
  ): MyResult<T> = TODO()
}

fun main(args: Array<String>) {
  val propertyReader = PropertyReader("/config.properties")
  val person =
    propertyReader
      .readAsInt("id")
      .flatMap<Int, Person> { id ->
        propertyReader
          .readAsString("firstName")
          .flatMap<String, Person> { firstName ->
            propertyReader
              .readAsString("lastName")
              .map<String, Person> { lastName -> Person(id, firstName, lastName) }
          }
      }
  person.unsafeForEachOrElse { println("SUCCESS: $it") } { println("FAILURE: $it") } { println("EMPTY") }
}
