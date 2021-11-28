package com.github.niqdev.rekursion

interface Kind<out F, out A>

// partially applied type constructor
typealias Kind2<F, A, B> = Kind<Kind<F, A>, B>
