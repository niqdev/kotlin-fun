// ---------- 3.1 ----------

fun compose0(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int = { x -> f(g(x)) }
fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int = { f(g(it)) }

val square: (Int) -> Int = { it * it }
fun triple(value: Int): Int = value * 3
val squareOfTriple: (Int) -> Int = compose(square, ::triple)
println(squareOfTriple(2))

// ---------- 3.2 ----------

// TODO
