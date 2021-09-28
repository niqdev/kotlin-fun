package com.github.niqdev.bool

// TODO delete
enum class TokenType {
  // single-character tokens
  LEFT_PAREN,
  RIGHT_PAREN,

  // one or two character tokens
  BANG,
  BANG_EQUAL,
  EQUAL_EQUAL,
  GREATER,
  GREATER_EQUAL,
  LESS,
  LESS_EQUAL,

  // literals
  STRING,
  NUMBER,

  // keywords
  AND,
  OR
}

// TODO key
sealed class Token {
  internal data class TokenInt(val value: Int) : Token()
  internal data class TokenString(val value: String) : Token()
  internal object TokenLeftParentheses : Token()
  internal object TokenRightParentheses : Token()
  internal object TokenBangEqual : Token()
  internal object TokenEqual : Token()
  internal object TokenEqualEqual : Token()
  internal object TokenGreater : Token()
  internal object TokenGreaterEqual : Token()
  internal object TokenLess : Token()
  internal object TokenLessEqual : Token()
  internal object TokenAnd : Token()
  internal object TokenOr : Token()
  internal object TokenNot : Token()

  companion object {
    fun pretty(token: Token) =
      when (token) {
        is TokenInt -> "Int(${token.value})"
        is TokenString -> "String(${token.value})"
        is TokenLeftParentheses -> "Symbol('(')"
        is TokenRightParentheses -> "Symbol(')')"
        is TokenBangEqual -> "Symbol(!=)"
        is TokenEqual -> "Symbol(=)"
        is TokenEqualEqual -> "Symbol(==)"
        is TokenGreater -> "Symbol(>)"
        is TokenGreaterEqual -> "Symbol(>=)"
        is TokenLess -> "Symbol(<)"
        is TokenLessEqual -> "Symbol(<=)"
        is TokenAnd -> "AND"
        is TokenOr -> "OR"
        is TokenNot -> "NOT"
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

 */

/**
 * expression   → literal
 *                | expression OR expression
 *                | expression AND expression
 *                | NOT expression
 * literal      → "true" | "false"
 */
sealed class Expression {
  class Binary(val left: Expression, val op: Token, val right: Expression) : Expression()
  // class Grouping(val expression: Expr) : Expr()
  // class Unary(val op: Token, val right: Expr) : Expr()
  class Literal(val value: Value) : Expression()
}

sealed class Value {
  internal object ValueTrue : Value()
  internal object ValueFalse : Value()
  internal data class ValueString(val string: String) : Value()
  internal data class ValueNumber(val int: String) : Value()
  // e.g. json path
  internal data class ValueKey(val key: String) : Value()
}
