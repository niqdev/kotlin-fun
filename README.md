# kotlin-fp

* Kotlin [docs](https://kotlinlang.org)

```bash
./gradlew tasks

./gradlew clean build
./gradlew test
./gradlew :modules:app:test --tests *AppTest
./gradlew run

# format code
./gradlew lintKotlin
./gradlew formatKotlin

# dowanload sources
./gradlew cleanIdea idea

# dependency tree
./gradlew :modules:app:dependencies
```

TODO
* format/lint
* dependabot
* version from git commit/tag
* travis
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
