plugins {
  id("org.jetbrains.kotlin.jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
}

repositories {
  mavenCentral()
}

task("runLox", JavaExec::class) {
  main = "com.github.niqdev.MainKt"
  classpath = sourceSets["main"].runtimeClasspath
}
