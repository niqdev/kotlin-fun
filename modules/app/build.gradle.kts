plugins {
  kotlin("jvm") version Versions.kotlin
  kotlin("plugin.serialization") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
  // https://kotlinlang.org/docs/kapt.html
  kotlin("kapt") version Versions.kapt
  application
  // https://docs.gradle.org/current/userguide/idea_plugin.html
  idea
}

repositories {
  mavenLocal()
  mavenCentral()
  maven(url = "https://dl.bintray.com/arrow-kt/arrow-kt/")
}

// TODO remove multiple dependency versions e.g. kotlin-stdlib
dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutines}")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinxSerialization}")

  // logging
  implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
  runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")

  // config
  implementation("com.sksamuel.hoplite:hoplite-core:${Versions.hoplite}")
  implementation("com.sksamuel.hoplite:hoplite-yaml:${Versions.hoplite}")

  // cli
  implementation("com.github.ajalt.clikt:clikt:${Versions.clikt}")

  // reactor
  implementation("io.projectreactor:reactor-core:${Versions.reactor}")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:${Versions.reactorKotlin}")

  // arrow
  implementation(platform("io.arrow-kt:arrow-stack:${Versions.arrow}"))
  implementation("io.arrow-kt:arrow-core")
  implementation("io.arrow-kt:arrow-fx-coroutines")
  implementation("io.arrow-kt:arrow-optics")
  kapt("io.arrow-kt:arrow-meta:${Versions.arrow}")

  // tests
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

idea {
  module.setDownloadJavadoc(true)
  module.setDownloadSources(true)
}

application {
  mainClass.set("com.github.niqdev.AppKt")
}
// TODO how to set JVM options for all ???
tasks.named<JavaExec>("run") {
  jvmArgs = listOf("-Dkotlinx.coroutines.debug")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}

task("runReactorExample", JavaExec::class) {
  main = "com.github.niqdev.reactor.ReactorExampleKt"
  classpath = sourceSets["main"].runtimeClasspath
}
task("runCliktExample", JavaExec::class) {
  main = "com.github.niqdev.clikt.CliktExampleKt"
  classpath = sourceSets["main"].runtimeClasspath
  args = listOf((project.properties.getOrElse("myArgs", { "--help" }) as String))
}
task("runJsonExample", JavaExec::class) {
  main = "com.github.niqdev.serialization.JsonExampleKt"
  classpath = sourceSets["main"].runtimeClasspath
}

// the task has precedence over .editorconfig
kotlinter {
  // "disabled_rules" is not picked up correctly
  disabledRules = arrayOf("no-wildcard-imports")
}
