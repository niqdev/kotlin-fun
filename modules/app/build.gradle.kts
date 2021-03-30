plugins {
  application
  id("org.jetbrains.kotlin.jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
  // https://kotlinlang.org/docs/kapt.html
  kotlin("kapt") version Versions.kapt
}

repositories {
  jcenter()
  mavenCentral()
  maven(url = "https://dl.bintray.com/arrow-kt/arrow-kt/")
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  // logging
  implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
  implementation("ch.qos.logback:logback-classic:${Versions.logback}")

  // arrow
  implementation("io.arrow-kt:arrow-core:${Versions.arrow}")
  implementation("io.arrow-kt:arrow-syntax:${Versions.arrow}")
  kapt("io.arrow-kt:arrow-meta:${Versions.arrow}")

  // tests
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  mainClass.set("com.github.niqdev.AppKt")
}
