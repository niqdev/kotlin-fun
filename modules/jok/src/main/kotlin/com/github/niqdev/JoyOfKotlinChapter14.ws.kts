import com.github.niqdev.MyResult
import com.github.niqdev.flatMap
import com.github.niqdev.map
import com.github.niqdev.orElse
import com.github.niqdev.unsafeForEachOrElse

object assertions {
  fun <T> assertCondition(
    value: T,
    f: (T) -> Boolean,
    message: String
  ): MyResult<T> =
    if (f(value)) MyResult(value)
    else MyResult.failure(IllegalStateException(message))

  fun assertTrue(
    condition: Boolean,
    message: String = "Assertion error: condition should be true"
  ):
    MyResult<Boolean> =
    assertCondition(condition, { x -> x }, message)

  fun assertFalse(
    condition: Boolean,
    message: String = "Assertion error: condition should be false"
  ):
    MyResult<Boolean> =
    assertCondition(condition, { x -> !x }, message)
}

val valid = assertions
  .assertTrue(1 > 0)
  .flatMap<Boolean, Boolean>()() { first ->
  assertions.assertFalse(3 < 1).map<Boolean, Boolean>()() { second -> first && second }
}

// ------------------------------

fun <A, B> retry(f: (A) -> B, times: Int, delayMillis: Long = 10): (A) -> MyResult<B> =
  { a ->
    fun loop(i: Int, result: MyResult<B>): MyResult<B> =
      result.orElse()() {
        when (i) {
          0 -> MyResult.Empty
          else -> {
            Thread.sleep(delayMillis)
            println("retry ${times - i}")
            loop(i, MyResult.of { f(a) })
          }
        }
      }
    loop(times - 1, MyResult.of { f(a) })
  }

fun unsafeShow(message: String): String =
  java.util.Random().nextInt(10).let {
    when {
      it < 8 -> throw IllegalStateException("boom")
      else -> message
    }
  }

retry<String, String>(::unsafeShow, 10, 100)("hello world")
  .unsafeForEachOrElse()() { println("SUCCESS: $it") }() { println("FAILURE: $it") }() { println("EMPTY") }

// ------------------------------

// see PropertyReader
// see xml

// value types
// https://cr.openjdk.java.net/~jrose/values/values-0.html

// property-based test examples
// https://github.com/pysaumont/fpinkotlin/tree/master/examples/kotlingradle/MyMultipleProject/common/src/test/kotlin/com/mydomain/mymultipleproject/common
