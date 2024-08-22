import com.github.niqdev.MyLazy

// Kotlin is supposed to be a strict language: everything is evaluated immediately
// function arguments are said to be `passed by value`, which means they're first evaluated and then the evaluated value is passed
// function arguments in Kotlin are often references i.e addresses, and these addresses are passed by value
// in lazy languages, arguments are said to be `passed by name`, which means unevaluated

// lazy constructs
// - boolean operators || and &&
//   `false` is an `absorbing element` for the `&& operation` and `true` is an `absorbing element` for the `|| operation`
// - if ... else
// - for loop
// - while loop
// - Sequence

// ------------------------------

// Kotlin provides a way to implement laziness through the use of a delegate
val myLazyValue: Boolean by Delegate()

// the Delegate class, which can have any name you choose, doesn't need to implement any interface,
// it must declare and implement the `getValue` function, which will be called through reflection
class Delegate {
  operator fun getValue(
    thisRef: Any?,
    property: kotlin.reflect.KProperty<*>,
  ): Boolean = TODO()

  // if you were declaring a `var` instead of a `val`, the Delegate class should also implement the corresponding function to set the value
  // operator fun setValue(thisRef: Any?, property: kotlin.reflect.KProperty<*>, value: Boolean) = TODO()
}

// standard delegates
val first: Boolean by lazy { true }
val second: Boolean by lazy { throw IllegalStateException() }

println("lazy evaluation: ${first || second}")

fun orStrict(
  a: Boolean,
  b: Boolean,
): Boolean = if (a) true else b

// if you pass it as argument it will still be invoked... not really lazy!
// orStrict(first, second)

// ------------------------------

// laziness can be represented by using a constant function
val x: () -> Int = { 42 }
x()

fun orLazy(
  a: () -> Boolean,
  b: () -> Boolean,
): Boolean = if (a()) true else b()

// - exception is not thrown
// - if you use the value twice, the function is called twice
// - called a `call by name evaluation`: the value isn't evaluated before being needed, but it's evaluated each time it's needed
// - same result by using a `fun function`, but you'd have to provide the return type for the function throwing an exception because Kotlin can't infer it
orLazy({ first }, { second })

// ---------- 9.1 ----------

val myFirst = MyLazy { true }
val mySecond = MyLazy { throw IllegalStateException() }

fun orMyLazy(
  a: MyLazy<Boolean>,
  b: MyLazy<Boolean>,
): Boolean = if (a()) true else b()

orMyLazy(myFirst, mySecond)

// ---------- 9.2 ----------
// ---------- 9.3 ----------

fun MyLazy<String>.concat(): (MyLazy<String>) -> MyLazy<String> = { value -> MyLazy { "${this.invoke()} | ${value()}" } }

// hello is evaluated only once
val hello =
  MyLazy {
    println("load hello")
    "hello"
  }
hello.concat()(
  MyLazy {
    println("load world")
    "world"
  },
).invoke()
hello.concat()(
  MyLazy {
    println("load world")
    "world"
  },
).invoke()

// ------------------------------

// see MyLazy and MyList and MyStream
