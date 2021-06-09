# kotlin-fun

Personal notes about Kotlin. The project's name was inspired by [this](https://www.youtube.com/watch?v=GrzM3jK3Y2E) talk!

* Kotlin [docs](https://kotlinlang.org/docs/home.html)

```bash
./gradlew tasks
./gradlew :modules:jok:tasks

./gradlew clean build
./gradlew test
./gradlew :modules:app:test --tests *AppTest
./gradlew run
./gradlew :modules:aws-local:run

# custom main tasks
./gradlew runReactorExample
./gradlew runCliktExample --args="--help"
./gradlew runJsonExample
./gradlew runMyStream
./gradlew runMyList --debug-jvm

# format code
./gradlew lintKotlin
./gradlew formatKotlin

# download sources
./gradlew cleanIdea idea

# dependency tree
./gradlew :modules:app:dependencies
```

## Modules

* Kotlin in Action [ [book](https://www.manning.com/books/kotlin-in-action) | [notes](modules/kia/src/main/kotlin/com/github/niqdev) ]
* The Joy of Kotlin [ [book](https://www.manning.com/books/the-joy-of-kotlin) | [notes](modules/jok/src/main/kotlin/com/github/niqdev) ]
* Functional Programming in Kotlin [ [book](https://www.manning.com/books/functional-programming-in-kotlin) | TODO ]
* Category Theory for Programmers [ [book](https://github.com/hmemcpy/milewski-ctfp-pdf) | TODO ]
* Crafting Interpreters [ [book](https://craftinginterpreters.com) | TODO ]
* [aws-local](modules/aws-local/README.md)

## Examples

* [x] dependency management: [Dependabot](https://docs.github.com/en/code-security/supply-chain-security/keeping-your-dependencies-updated-automatically)
* [x] linting/formatting: [ktlint](https://ktlint.github.io)
* [x] Gradle multi-project: [doc](https://docs.gradle.org/current/userguide/multi_project_builds.html)
* [x] Gradle `buildSrc`
* [ ] github action
    * [ ] CI/CD
    * [ ] release management (git commit/tag)
* [ ] markdown documentation: [dokka](https://kotlin.github.io/dokka) vs [docusaurus](https://docusaurus.io)
* [ ] property based tests with [Kotest](https://kotest.io)
* [ ] http with [Ktor](https://ktor.io)
    * [x] logging
    * [x] config with [hoplite](https://github.com/sksamuel/hoplite)
    * [ ] server
    * [ ] client
* [ ] postgres
* [ ] kafka
