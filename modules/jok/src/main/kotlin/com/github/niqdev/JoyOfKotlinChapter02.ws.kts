// the word `effects` means all interactions with the outside world, such as writing to the console, to a file, to a database, or to a network, and also mutating any element outside the component's scope
// - programming with side effects is wrong in all cases
// - safe programs are built by composing functions that take an argument and return a value: `single responsibility` principle
// when a method or function returns a value and has an effect, this is called a `side effect`
// a function without side effects is called `pure function`
// code that neither mutates nor depends on the external world is said to be `referentially transparent`
// `substitution model`: it's always possible to replace a function call or any referentially transparent expression with its value

// ------------------------------
