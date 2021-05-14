// `null pointer`: most data is represented by a reference pointing to it, so the most obvious way to represent the absence of data is to use a pointer to nothing
// in Kotlin, a `reference` is a pointer to a value
// in most programming languages, `references` can be changed to point to a new value: `variable` is often used as a replacement for reference
// `variable references`: some references can be created `null` and then changed to point to value. They can even be changed again to point to null if data is removed
// `constants`: can't be created without making them point to a value, and once this is done, they can't be made to point to a different value
// i.e. `var` vs `val` (final)

// NullPointerException: is raised when an identifier is de-referenced and found to be pointing to nothing
// https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare
// `business null`: the use of `null` to represent absence of data is dangerous

// an `Int` reference can never be set to null but an `Int?` reference can

// ------------------------------

// see Option
