### lox

> TODO

## Development

```bash
./gradlew runLox
```

## Resources

* [awesome-compilers](https://github.com/aalhour/awesome-compilers)
* Structure and Interpretation of Computer Programs [ [book](https://mitpress.mit.edu/sites/default/files/sicp/index.html) | [course](https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-001-structure-and-interpretation-of-computer-programs-spring-2005) ]
* [Compilers: Principles, Techniques, and Tools](https://suif.stanford.edu/dragonbook)
* [Let's Build a Compiler](https://xmonader.github.io/letsbuildacompiler-pretty/about.html)
* [Build Your Own Lisp](https://buildyourownlisp.com)

<!--
# TODO parser: s-expression, json, arithmetic expression
https://matt.might.net/articles/parsing-s-expressions-scala
https://notes.eatonphil.com/writing-a-simple-json-parser.html
https://1oo1.github.io/2019/12/20/Write-a-json-parser
https://stackoverflow.com/questions/17063690/how-to-write-a-basic-json-parsing-class
https://github.com/h2database/h2database/blob/master/h2/src/main/org/h2/command/Parser.java
https://github.com/mhewedy/eureka-klient/blob/master/src/main/kotlin/helpers/json/Deserializer.kt
https://github.com/sirthias/parboiled/wiki
https://tpolecat.github.io/atto
https://gist.github.com/DmitrySoshnikov/2a434dda67019a4a7c37
-->

## Notes

<p align="center">
  <img src="../../doc/ci-mountain.png" alt="mountain">
</p>

1) scanning (or lexing or lexical analysis)
    * in greek *lex* means *word*
    * a `scanner` (or `lexer`) takes in the linear stream of characters and chunks them together into a series of words or `tokens`
    * a token can be a single char or several chars (numbers, string literal, identifiers)
    * ignores whitespaces or comments

2) parsing
    * where a syntax gets a grammar: the ability to compose larger expressions and statements out of smaller parts
    * a `parser` takes the flat sequence of tokens and builds a tree structure that mirrors the nested nature of the grammar: a `parse tree` or `abstract syntax tree` or `syntax tree` or `ast` or `tree`

3) static analysis
    * the first bit of analysis that most languages do is called `binding` or `resolution`
    * for each identifier, we find out where that name is defined and wire the two together. This is where scope comes into play — the region of source code where a certain name can be used to refer to a certain declaration
    * if the language is statically typed, this is when we type check and report `type errors`
    * all this semantic insight that is visible to us from analysis needs to be stored somewhere: often as attributes on the syntax tree itself, other times in a lookup table off to the side called `symbol table`

4) intermediate representations
    * well-known styles of IRs: `control flow graph`, `static single-assignment`, `continuation-passing style`, `three-address code`
    * support multiple source languages and target platforms

5) optimization
    * swap program with a different one that has the same semantics, but it's implemented more efficiently
    * e.g. constant folding: if some expression always evaluates to the exact same value, we can do the evaluation at compile time and replace the code for the expression with its result


6) code generation
    * generating code, where "code" here usually refers to the kind of primitive assembly-like instructions a CPU runs
    * Do we generate instructions for a real CPU or a virtual one?
    * native machine code is lightning fast, but generating it is a lot of work and it's not portable
    * virtual machine code is portable, generally called `bytecode` because each instruction is often a single byte long

7) virtual machine
    * a (language or process) virtual machine (VM) is a program that emulates a hypothetical chip supporting a virtual architecture at runtime
    * running bytecode in a VM is slower than translating it to native code ahead of time because every instruction must be simulated at runtime each time it executes. In return, you get simplicity and portability

8) runtime
    * if we compiled it to machine code, we simply tell the operating system to load the executable and off it goes
    * if we compiled it to bytecode, we need to start up the VM and load the program into that

* `1)` to `3)` are considered `front end` of the implementation
* from `6)` it's `back end`
* alternative *compiler-compilers* (parser generators): Lex, Yacc, Bison
* *single-pass compilers*: some simple compilers interleave parsing, analysis, and code generation so that they produce output code directly in the parser, without ever allocating any syntax trees or other IR
* *tree-walk interpreters*: begin executing code right after parsing it to an AST: to run the program, the interpreter traverses the syntax tree one branch and leaf at a time, evaluating each node as it goes
* *transpilers* are source-to-source compiler or a transcompiler: write a front end for your language. Then, in the back end, instead of doing all the work to lower the semantics to some primitive target language, you produce a string of valid source code for some other language that's about as high level as yours
* *just-in-time compilation*: the fastest way to execute code is by compiling it to machine code

What's the difference between a compiler and an interpreter?
* compiling is an implementation technique that involves translating a source language to some other—usually lower-level—form. When you generate bytecode or machine code, you are compiling. When you transpile to another high-level language, you are compiling too
* when we say a language implementation "is a compiler", we mean it translates source code to some other form but doesn't execute it. The user has to take the resulting output and run it themselves
* when we say an implementation "is an interpreter", we mean it takes in source code and executes it immediately. It runs programs "from source"
