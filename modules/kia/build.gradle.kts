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
