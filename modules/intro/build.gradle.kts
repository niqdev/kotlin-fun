plugins {
  //application
  id("org.jetbrains.kotlin.jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
}

repositories {
  jcenter()
}

dependencies {
  implementation(kotlin("script-runtime"))
}

// TODO ./gradlew :modules:app:run
// TODO ./gradlew run -Pmain=com.github.niqdev.HelloWorld
//application {
//  mainClass.set(project.findProperty("main").toString())
//}
