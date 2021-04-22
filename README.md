# kotlin-fp

* Kotlin [docs](https://kotlinlang.org/docs/home.html)

```bash
./gradlew tasks

./gradlew clean build
./gradlew test
./gradlew :modules:app:test --tests *AppTest
./gradlew run

# format code
./gradlew lintKotlin
./gradlew formatKotlin

# download sources
./gradlew cleanIdea idea

# dependency tree
./gradlew :modules:app:dependencies
```

## Modules

* Kotlin in Action [ [book](https://www.manning.com/books/kotlin-in-action) | [repository](https://github.com/Kotlin/kotlin-in-action) | [notes](modules/kia/src/main/kotlin/com/github/niqdev) ]
* The Joy of Kotlin [ [book](https://www.manning.com/books/the-joy-of-kotlin) | [repository](https://github.com/pysaumont/fpinkotlin) | [notes](modules/jok/src/main/kotlin/com/github/niqdev) ]
* Functional Programming in Kotlin [ [book](https://www.manning.com/books/functional-programming-in-kotlin) ]

## TODO

* rename: kotlin-playground
* format/lint
* dokka (markdown)
* dependabot
* version from git commit/tag
* ~~travis~~ github action
* multi-project
* examples
    - logging
    - property based tests
    - http (ktor)
    - postgres
    - kafka
* fp
    - re-implement basic types
    - arrows
