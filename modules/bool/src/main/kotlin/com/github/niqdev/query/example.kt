package com.github.niqdev.query

sealed interface Operator {
  data object Eq : Operator
  data object Neq : Operator
  data object Lt : Operator
  data object Lte : Operator
  data object Gt : Operator
  data object Gte : Operator
}

fun Operator.pretty(): String =
  when (this) {
    is Operator.Eq -> "=="
    is Operator.Neq -> "!="
    is Operator.Lt -> "<"
    is Operator.Lte -> "<="
    is Operator.Gt -> ">"
    is Operator.Gte -> ">="
  }

// ////////////////////////////

// TODO type decoder (int/string/date) and operator (eq/neq/gt/ls)

sealed interface FilterPredicate {
  data object All : FilterPredicate
  data class Filter(val name: String, val value: String) : FilterPredicate
  // TODO replace with NonEmptyList
  data class In(val head: Filter, val tail: List<Filter> = emptyList()) : FilterPredicate
}

// ////////////////////////////

sealed interface ParserResult<T> {
  data class Success<T>(val value: T) : ParserResult<T>
  data class Failure<T>(val error: Throwable) : ParserResult<T>
}

fun <T> Result<T>.toParserResult(): ParserResult<T> =
  this.fold({ ParserResult.Success(it) }, { ParserResult.Failure(it) })

inline fun <reified T> ParserResult<T>.getOrNull(): T? =
  when (this) {
    is ParserResult.Success -> this.value
    is ParserResult.Failure -> null
  }

inline fun <reified T, reified R> ParserResult<T>.fold(
  s: (T) -> R,
  f: (Throwable) -> R
): R =
  when (this) {
    is ParserResult.Success -> s(this.value)
    is ParserResult.Failure -> f(this.error)
  }

inline fun <reified T> ParserResult<T>.isSuccess(): Boolean =
  fold({ true }, { false })

// ////////////////////////////

sealed interface FilterParser {
  fun parse(value: String): ParserResult<FilterPredicate>
}

data object QueryParser : FilterParser {
  override fun parse(value: String): ParserResult<FilterPredicate> =
    runCatching {
      // TODO Decoder with validation
      val filters = value.split("&").fold(emptyList<FilterPredicate.Filter>()) { xs, raw ->
        val x = raw.split("=")
        xs + FilterPredicate.Filter(x.first(), x.last())
      }

      when (filters.size) {
        0 -> FilterPredicate.All
        1 -> filters.first()
        else -> FilterPredicate.In(filters.first(), filters.drop(1))
      }
    }.toParserResult()
}
