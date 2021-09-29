package com.github.niqdev.bool

// TODO IN, MATCH
sealed class Token {
  object TokenLeftParentheses : Token()
  object TokenRightParentheses : Token()
  object TokenMinus : Token()
  object TokenBangEqual : Token()
  object TokenEqual : Token()
  object TokenEqualEqual : Token()
  object TokenGreater : Token()
  object TokenGreaterEqual : Token()
  object TokenLess : Token()
  object TokenLessEqual : Token()
  object TokenTrue : Token()
  object TokenFalse : Token()
  object TokenAnd : Token()
  object TokenOr : Token()
  object TokenNot : Token()
  data class TokenNumber(val value: Int) : Token()
  data class TokenString(val value: String) : Token()
  data class TokenKey(val value: String) : Token()

  companion object {
    val identifiers = mapOf(
      "TRUE" to TokenTrue,
      "FALSE" to TokenFalse,
      "AND" to TokenAnd,
      "OR" to TokenOr,
      "NOT" to TokenNot
    )

    fun pretty(token: Token) =
      when (token) {
        is TokenLeftParentheses -> "("
        is TokenRightParentheses -> ")"
        is TokenMinus -> "-"
        is TokenBangEqual -> "!="
        is TokenEqual -> "="
        is TokenEqualEqual -> "=="
        is TokenGreater -> ">"
        is TokenGreaterEqual -> ">="
        is TokenLess -> "<"
        is TokenLessEqual -> "<="
        is TokenTrue -> "TRUE"
        is TokenFalse -> "FALSE"
        is TokenAnd -> "AND"
        is TokenOr -> "OR"
        is TokenNot -> "NOT"
        is TokenNumber -> "Number(${token.value})"
        is TokenString -> "String(${token.value})"
        is TokenKey -> "Key(${token.value})"
      }
  }
}
/*

https://sheabunge.github.io/boolcalc/
https://craftinginterpreters.com/appendix-i.html#expressions
https://cs.au.dk/~danvy/dProgSprog16/Lecture-notes/lecture-notes_week-3.html
https://xmonader.github.io/letsbuildacompiler-pretty/tutor06_booleanexpressions.html
https://compilers.iecc.com/crenshaw/tutor6.txt
https://www.cs.unb.ca/~wdu/cs4613/a2ans.htm
https://docs.oracle.com/cd/E13203_01/tuxedo/tux80/atmi/fml0516.htm
https://stackoverflow.com/questions/63493679/backus-naur-form-with-boolean-algebra-problem-with-brackets-and-parse-tree

https://cs.wmich.edu/~gupta/teaching/cs4850/sumII06/The%20syntax%20of%20C%20in%20Backus-Naur%20form.htm
https://www.cs.unc.edu/~plaisted/comp455/Algol60.pdf

expression     → logic_or
logic_or       → logic_and ( "or" logic_and )*
logic_and      → equality ( "and" equality )*
equality       → comparison ( ( "!=" | "==" ) comparison )*
comparison     → unary ( ( ">" | ">=" | "<" | "<=" ) unary )*
unary          → ( "!" | "-" ) unary | primary
primary        → NUMBER | STRING | "true" | "false" | "(" expression ")"

---

<b-expression> ::= <b-term> [OR <b-term>]*
<b-term>       ::= <not-factor> [AND <not-factor>]*
<not-factor>   ::= [NOT] <b-factor>
<b-factor>     ::= <b-literal> | <b-variable> | <relation>
<relation>     ::= | <expression> [<relop> <expression>]
<expression>   ::= <term> [<addop> <term>]*
<term>         ::= <signed factor> [<mulop> factor]*
<signed factor>::= [<addop>] <factor>
<factor>       ::= <integer> | <variable> | (<b-expression>)

<expression>  ::= <expression> | <disjunction>
<disjunction> ::= <disjunction>

expression     → logic_or ;
logic_or       → logic_and ( "or" logic_and )* ;
logic_and      → equality ( "and" equality )* ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;

<b-expression> ::= <b-term> [<orop> <b-term>]*
<b-term>       ::= <not-factor> [AND <not-factor>]*
<not-factor>   ::= [NOT] <b-factor>
<b-factor>     ::= <b-literal>

<expression>  ::= <expression>  ↔ <implication> | <implication>
<implication> ::= <implication> → <disjunction> | <disjunction>
<disjunction> ::= <disjunction> ∨ <conjunction> | <conjunction>
<conjunction> ::= <conjunction> ∧ <negation>    | <negation>
<negation>    ::= ¬ <negation> | <variable> | ( <expression> )
<variable>    ::= p | q | r | s

true OR false AND true OR NOT false
(((true OR false) AND true) OR (NOT false))

expression   → literal
               | expression OR expression
               | expression AND expression
               | NOT expression
literal      → "true" | "false"
*/
sealed class Expression {
  class Binary(val left: Expression, val token: Token, val right: Expression) : Expression()
  class Grouping(val expression: Expression) : Expression()
  // TODO should token be replaced by sealed class only for NOT and MINUS
  class Unary(val token: Token, val right: Expression) : Expression()
  class Literal(val value: Value) : Expression()

  companion object {

    fun pretty(expression: Expression): String =
      when (expression) {
        is Binary -> parenthesize(Token.pretty(expression.token), expression.left, expression.right)
        is Grouping -> parenthesize("group", expression.expression)
        is Literal -> Value.pretty(expression.value)
        is Unary -> parenthesize(Token.pretty(expression.token), expression.right)
      }

    private fun parenthesize(name: String, vararg expressions: Expression): String {
      val values = expressions.fold("", { result, expr -> "$result ${pretty(expr)}" })
      return "($name$values)"
    }
  }
}

sealed class Value {
  internal object ValueTrue : Value()
  internal object ValueFalse : Value()
  internal data class ValueNumber(val number: Int) : Value()
  internal data class ValueString(val string: String) : Value()
  // e.g. json path
  internal data class ValueKey(val key: String) : Value()

  companion object {

    fun pretty(value: Value): String =
      when (value) {
        is ValueTrue -> ""
        is ValueFalse -> ""
        is ValueNumber -> "Number(${value.number})"
        is ValueString -> "String(${value.string})"
        is ValueKey -> "String(${value.key})"
      }
  }
}
