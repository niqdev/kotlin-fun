package com.github.niqdev.bool

/*
https://cs.au.dk/~danvy/dProgSprog16/Lecture-notes/lecture-notes_week-3.html
https://xmonader.github.io/letsbuildacompiler-pretty/tutor06_booleanexpressions.html
https://compilers.iecc.com/crenshaw/tutor6.txt
https://www.cs.unb.ca/~wdu/cs4613/a2ans.htm
https://docs.oracle.com/cd/E13203_01/tuxedo/tux80/atmi/fml0516.htm

https://cs.wmich.edu/~gupta/teaching/cs4850/sumII06/The%20syntax%20of%20C%20in%20Backus-Naur%20form.htm
https://www.cs.unc.edu/~plaisted/comp455/Algol60.pdf
 */

object StringLexer {

  // TODO Validated<NonEmptyList<Error>, List<Token>>
  fun tokenize(input: String): List<Token> {

    tailrec fun loop(index: Int, result: List<Token>): List<Token> =
      when {
        index < input.length -> {
          when (val c = input[index]) {
            '(' -> TODO()
            ')' -> TODO()
            '!' -> TODO()
            '=' -> TODO()
            '<' -> {
              val token = Token.TokenLess
              loop(index + 1, result + token)
            }
            '>' -> TODO()
            '&' -> TODO()
            '|' -> TODO()
            '"' -> TODO()

            ' ', '\t', '\n', '\r', '\b' ->
              loop(index + 1, result)
            else -> {
              when {
                c.isDigit() -> {
                  val tokenString = scanNumber()(input.substring(index))
                  loop(index + tokenString.length, result + Token.TokenInt(tokenString.toInt()))
                }
                c.isAlpha() -> {
                  val tokenString = scanString()(input.substring(index))
                  loop(index + tokenString.length, result + Token.TokenString(tokenString))
                }
                else -> error("invalid char $c at position $index")
              }
            }
          }
        }
        else -> result
      }

    return loop(0, listOf())
  }

  fun scan(): ((Char) -> Boolean) -> (String) -> String =
    { predicate ->
      { input ->

        tailrec fun loop(index: Int, result: String): String =
          when {
            // end of the input string
            index >= input.length -> result
            // append valid char
            predicate(input[index]) -> loop(index + 1, result + input[index])
            else -> result
          }

        loop(0, "")
      }
    }

  fun scanNumber(): (String) -> String = scan()(Char::isDigit)
  fun scanString(): (String) -> String = scan()(Char::isAlpha)
}

private fun Char.isDigit(): Boolean = this in '0'..'9'
private fun Char.isAlpha(): Boolean = this in 'a'..'z' || this in 'A'..'Z' || "._-".contains(this)

fun main() {
  println(StringLexer.tokenize("8 < 42 aaa.bbb 56").map { Token::pretty })
}
