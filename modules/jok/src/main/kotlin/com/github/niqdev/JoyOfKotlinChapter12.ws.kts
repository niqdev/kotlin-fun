// `pure functions` are defined as functions without any observable `side effects`
// an effect is anything that can be observed from outside the program
// the role of a function is to return a value, and side effects are anything besides the returned value that's observable from the outside of the function
// it's called a side effect because it comes in addition to the value that's returned
// by contrast, an `effect` is like a side effect, but it's the main (and generally unique) role of a program
// referential transparency principle

// an effect is generally applied to a value, a pure effect can be modeled as a special kind of function, returning no value
// (T) -> Unit
