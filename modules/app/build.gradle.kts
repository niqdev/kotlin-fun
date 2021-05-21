plugins {
  application
  // https://docs.gradle.org/current/userguide/idea_plugin.html
  idea
  kotlin("jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
  // https://kotlinlang.org/docs/kapt.html
  kotlin("kapt") version Versions.kapt
}

repositories {
  mavenCentral()
  maven(url = "https://dl.bintray.com/arrow-kt/arrow-kt/")
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")

  // logging
  implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
  implementation("ch.qos.logback:logback-classic:${Versions.logback}")

  // config
  implementation("com.sksamuel.hoplite:hoplite-core:${Versions.hoplite}")
  implementation("com.sksamuel.hoplite:hoplite-yaml:${Versions.hoplite}")

  // arrow
  implementation("io.arrow-kt:arrow-core:${Versions.arrow}")
  kapt("io.arrow-kt:arrow-meta:${Versions.arrow}")

  // tests
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  mainClass.set("com.github.niqdev.AppKt")
}

// TODO how to run multiple main
// TODO ./gradlew :modules:app:run
// TODO ./gradlew run -Pmain=com.github.niqdev.HelloWorld
//application {
//  mainClass.set(project.findProperty("main").toString())
//}

idea {
  module.setDownloadJavadoc(true)
  module.setDownloadSources(true)
}

// TODO remove multiple dependency of kotlin-stdlib 1.4.20 and 1.4.32 ???
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}
