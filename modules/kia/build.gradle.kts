plugins {
  id("org.jetbrains.kotlin.jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("script-runtime"))
}
