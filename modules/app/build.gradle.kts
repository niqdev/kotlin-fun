plugins {
  // https://kotlinlang.org/docs/kapt.html
  kotlin("kapt")
  kotlin("plugin.serialization") version Versions.kotlin
  id("com.adarshr.test-logger") version 3.2.0

  application
  // https://docs.gradle.org/current/userguide/idea_plugin.html
  idea
}

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

  // json
  implementation("com.ubertob.kondor:kondor-core:${Versions.kondor}")
  testImplementation("com.ubertob.kondor:kondor-tools:${Versions.kondor}")

  // reactor
  implementation("io.projectreactor:reactor-core:${Versions.reactor}")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:${Versions.reactorKotlin}")

  // arrow
  implementation(platform("io.arrow-kt:arrow-stack:${Versions.arrow}"))
  implementation("io.arrow-kt:arrow-core")
  implementation("io.arrow-kt:arrow-fx-coroutines")
  implementation("io.arrow-kt:arrow-optics")
  kapt("io.arrow-kt:arrow-meta:${Versions.arrowMeta}")

  // tests
  testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
  testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
  testImplementation("io.kotest:kotest-assertions-json:${Versions.kotest}")
  testImplementation("io.kotest:kotest-property:${Versions.kotest}")
  // TODO fix conflict
  //testImplementation("org.jetbrains.kotlin:kotlin-test")
  //testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

idea {
  module.setDownloadJavadoc(true)
  module.setDownloadSources(true)
}

application {
  mainClass.set("com.github.niqdev.AppKt")
}
tasks.named<JavaExec>("run") {
  // TODO how to set JVM options for all ???
  jvmArgs = listOf("-Dkotlinx.coroutines.debug")
  // TODO (verify) this should allow to pass any "-Dconfig.key=value" property
  systemProperties(System.getProperties().mapKeys { it.key.toString() }.toMap())
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

tasks.withType<Test> {
  // required for kotest
  useJUnitPlatform()
  // print to stdout
  testLogging.showStandardStreams = true
}

apply<GreetingPlugin>()
tasks.build {
  dependsOn("hello")
}
