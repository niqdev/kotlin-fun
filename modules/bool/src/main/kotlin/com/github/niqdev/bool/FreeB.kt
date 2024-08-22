package com.github.niqdev.bool

// see https://github.com/niqdev/scala-fp/blob/master/modules/fp/src/main/scala/com/github/niqdev/free/FreeB.scala
sealed interface FreeB<T> {
  data class Pure<T>(
    val value: T,
  ) : FreeB<T>

  data class Grouping<T>(
    val fb: FreeB<T>,
  ) : FreeB<T>

  class True<T> : FreeB<T>

  class False<T> : FreeB<T>

  data class And<T>(
    val left: FreeB<T>,
    val right: FreeB<T>,
  ) : FreeB<T>

  data class Or<T>(
    val left: FreeB<T>,
    val right: FreeB<T>,
  ) : FreeB<T>

  data class Not<T>(
    val right: FreeB<T>,
  ) : FreeB<T>
}

// free boolean algebra interpreter
// TODO Validated<NonEmptyList<Error>, Boolean>
private fun <T> FreeB<T>.run(f: (T) -> Boolean): Boolean =
  when (this) {
    is FreeB.Pure -> f(value)
    is FreeB.Grouping -> fb.run(f)
    is FreeB.True -> true
    is FreeB.False -> false
    is FreeB.And -> left.run(f) && right.run(f)
    is FreeB.Or -> left.run(f) || right.run(f)
    is FreeB.Not -> !right.run(f)
  }

fun FreeB<Predicate>.run(): Boolean = run { evalPredicate()(it) }

fun FreeB<Predicate>.pretty(): String =
  when (this) {
    is FreeB.Pure -> value.pretty()
    is FreeB.Grouping -> "(${fb.pretty()})"
    is FreeB.True -> "true"
    is FreeB.False -> "false"
    is FreeB.And -> "(${left.pretty()} AND ${right.pretty()}"
    is FreeB.Or -> "(${left.pretty()} OR ${right.pretty()}"
    is FreeB.Not -> "(NOT ${right.pretty()})"
  }

// TODO type class / higher-kinded - Predicate<T> and F<Predicate> ?
// TODO investigate "Fix[F]" https://github.com/precog/matryoshka and https://github.com/higherkindness/droste
sealed interface Predicate {
  // TODO potentially add an extra layer i.e. evaluate expressions - "3 > (3 + 2)"
  data class Identity(
    val id: Value,
  ) : Predicate

  // TODO should i move "is FreeB.Pure && is Predicate.Identity" in parser to have cleaner model e.g. Predicate.Greater(Predicate, Predicate)
  // TODO should i enforce type? advanced kotlin pattern matching on generics sucks! e.g. Greater<T>(left: T, right: T) or Greater<T>(Value<T>, Value<T>)
  data class Greater(
    val left: Identity,
    val right: Identity,
  ) : Predicate

  // TODO this is semantically wrong: you can't have "3 > (3 == 2)" i.e. right cannot be a FreeB<Predicate>
  data class GreaterEqual(
    val left: FreeB<Predicate>,
    val right: FreeB<Predicate>,
  ) : Predicate

  data class Less(
    val left: FreeB<Predicate>,
    val right: FreeB<Predicate>,
  ) : Predicate

  data class LessEqual(
    val left: FreeB<Predicate>,
    val right: FreeB<Predicate>,
  ) : Predicate

  data class EqualEqual(
    val left: FreeB<Predicate>,
    val right: FreeB<Predicate>,
  ) : Predicate

  data class BangEqual(
    val left: FreeB<Predicate>,
    val right: FreeB<Predicate>,
  ) : Predicate
  // class In(val left: FreeB<Predicate>, vararg items: Value) : Predicate
  // data class Match(val left: FreeB<Predicate>, val regex: String) : Predicate
}

// TODO incomplete: chain evaluation Number / String / Version
@Suppress("ktlint:standard:unnecessary-parentheses-before-trailing-lambda")
private fun evalPredicate(): (Predicate) -> Boolean =
  { predicate ->
    when (predicate) {
      is Predicate.Greater ->
        // evalNumber(predicate.left, predicate.right)() { l, r -> l.int > r.int }
        when {
          predicate.left.id is Value.Number && predicate.right.id is Value.Number ->
            predicate.left.id.int > predicate.right.id.int
          // TODO custom comparison logic e.g. ADT VERSION
          predicate.left.id is Value.String && predicate.right.id is Value.String ->
            predicate.left.id.string > predicate.right.id.string
          else -> error("invalid predicate operands: [left=${predicate.left}][right=${predicate.right}]")
        }
      is Predicate.Less ->
        evalNumber(predicate.left, predicate.right)() { l, r -> l.int < r.int }
      is Predicate.EqualEqual ->
        evalNumber(predicate.left, predicate.right)() { l, r -> l.int == r.int }
      else -> error("invalid predicate: $predicate")
    }
  }

// TODO Option/Either/Validated
private fun FreeB<Predicate>.toNumber(): Value.Number? =
  when {
    this is FreeB.Pure && value is Predicate.Identity && value.id is Value.Number -> value.id
    else -> null
  }

private fun evalNumber(
  left: FreeB<Predicate>,
  right: FreeB<Predicate>,
): ((Value.Number, Value.Number) -> Boolean) -> Boolean =
  { compare ->
    val l = left.toNumber()
    val r = right.toNumber()

    // TODO flatMap
    if (l != null && r != null) {
      compare(l, r)
    } else {
      error("invalid predicate operands: [left=$left][right=$right]")
    }
  }

fun Predicate.pretty(): String =
  when (this) {
    is Predicate.Identity -> id.pretty()
    is Predicate.Greater -> "(${left.pretty()} > ${right.pretty()})"
    is Predicate.GreaterEqual -> "(${left.pretty()} >= ${right.pretty()})"
    is Predicate.Less -> "(${left.pretty()} < ${right.pretty()})"
    is Predicate.LessEqual -> "(${left.pretty()} <= ${right.pretty()})"
    is Predicate.EqualEqual -> "(${left.pretty()} == ${right.pretty()})"
    is Predicate.BangEqual -> "(${left.pretty()} != ${right.pretty()})"
  }

sealed interface Value {
  data class Number(
    val int: Int,
  ) : Value

  data class String(
    val string: kotlin.String,
  ) : Value
}

fun Value.pretty(): String =
  when (this) {
    is Value.Number -> "Number($int)"
    is Value.String -> "String($string)"
  }

fun main() {
  val predicates: FreeB<Predicate> =
    FreeB.And(
      left =
        FreeB.Pure(
          Predicate.Less(
            FreeB.Pure(Predicate.Identity(Value.Number(8))),
            FreeB.Pure(Predicate.Identity(Value.Number(42))),
          ),
        ),
      right =
        FreeB.Pure(
          // TODO example without nested FreeB.Pure
          Predicate.Greater(
            Predicate.Identity(Value.Number(6)),
            Predicate.Identity(Value.Number(3)),
          ),
        ),
    )

  println(predicates.pretty())
  println(predicates.run())
}
