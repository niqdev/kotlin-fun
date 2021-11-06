package com.github.niqdev

sealed interface MyInput : java.io.Closeable {
  fun readString(message: String = ""): MyResult<Pair<String, MyInput>>
  fun readInt(message: String = ""): MyResult<Pair<Int, MyInput>>
}

abstract class AbstractReader(private val reader: java.io.BufferedReader) : MyInput {

  private fun <T> readLine(): ((String) -> T) -> MyResult<Pair<T, MyInput>> =
    { f ->
      runCatching {
        reader.readLine().let {
          when {
            it.isEmpty() -> MyResult()
            else -> MyResult(f(it) to this)
          }
        }
      }.getOrElse { MyResult.failure(it) }
    }

  override fun readString(message: String): MyResult<Pair<String, MyInput>> =
    readLine<String>()() { it }

  override fun readInt(message: String): MyResult<Pair<Int, MyInput>> =
    readLine<Int>()() { it.toInt() }

  override fun close() =
    reader.close()
}

class ConsoleReader(reader: java.io.BufferedReader) : AbstractReader(reader) {
  override fun readString(message: String): MyResult<Pair<String, MyInput>> {
    print(message)
    return super.readString(message)
  }

  override fun readInt(message: String): MyResult<Pair<Int, MyInput>> {
    print(message)
    return super.readInt(message)
  }

  companion object {
    operator fun invoke(): ConsoleReader =
      ConsoleReader(java.io.BufferedReader(java.io.InputStreamReader(System.`in`)))
  }
}

class FileReader(reader: java.io.BufferedReader) : AbstractReader(reader), AutoCloseable {
  companion object {
    operator fun invoke(path: String): MyResult<MyInput> =
      try { MyResult(FileReader(java.io.File(path).bufferedReader())) } catch (e: Exception) { MyResult.failure(e) }
  }
}

// ---------- 12.2 ----------

data class Person(val id: Int, val firstName: String, val lastName: String)

private fun person(input: MyInput): MyResult<Pair<Person, MyInput>> =
  input
    .readInt("enter id: ")
    .flatMap<Pair<Int, MyInput>, Pair<Person, MyInput>>()() { id ->
    id.second.readString("enter firstName: ")
      .flatMap<Pair<String, MyInput>, Pair<Person, MyInput>>()() { firstName ->
      firstName.second.readString("enter lastName: ")
        .map<Pair<String, MyInput>, Pair<Person, MyInput>>()() { lastName ->
        Person(id.first, firstName.first, lastName.first) to lastName.second
      }
    }
  }

// ---------- 12.3 ----------

// closing the resources with `use`

// TODO
private fun readPersonsFromFile(path: String): MyResult<MyList<Person>> =
  FileReader(path).map<MyInput, MyList<Person>>()() { it.use { input -> MyStream<Person>().unfold<Person, MyInput>()(input)(::person).toList() } }

// ------------------------------

private fun exampleString(): Unit =
  ConsoleReader()
    .readString("enter your name: ")
    .map<Pair<String, MyInput>, String>()() { it.first }
    .map<String, String>()() { "Hello: $it" }
    .unsafeForEachOrElse()(::println)() { println("error: $it") }() { println("empty") }

private fun exampleInt(): Unit =
  ConsoleReader()
    .readInt("enter your age: ")
    .map<Pair<Int, MyInput>, Int>()() { it.first }
    .map<Int, String>()() { "You look younger than $it" }
    .unsafeForEachOrElse()(::println)() { println("error: $it") }() { println("empty") }

// TODO StackOverflowError
private fun examplePerson(): MyList<Person> =
  MyStream<Person>().unfold<Person, MyInput>()(ConsoleReader())(::person).toList()

// ok but never prints the parsed list
private fun examplePersonList(): MyResult<MyList<Person>> =
  MyList<Person>().unfoldResult<Person, MyInput>()(ConsoleReader())(::person)

fun main() {
  // exampleString()
  // exampleInt()
  examplePerson().forEach()(::println)
  println(examplePersonList())
}
