plugins {
  // https://kotlinlang.org/docs/kapt.html
  //kotlin("kapt")
  kotlin("plugin.serialization") version "1.9.20"
  alias(libs.plugins.test.logger)

  application
  // https://docs.gradle.org/current/userguide/idea_plugin.html
  idea
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

  // logging
  implementation("org.slf4j:slf4j-api:2.0.9")
  runtimeOnly("ch.qos.logback:logback-classic:1.4.11")

  // config
  implementation("com.sksamuel.hoplite:hoplite-core:2.7.5")
  implementation("com.sksamuel.hoplite:hoplite-yaml:2.7.5")

  // cli
  implementation("com.github.ajalt.clikt:clikt:4.2.0")

  // json
  implementation("com.ubertob.kondor:kondor-core:2.1.1")
  testImplementation("com.ubertob.kondor:kondor-tools:2.1.1")

  // reactor
  implementation("io.projectreactor:reactor-core:3.5.10")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

  // arrow
  implementation(platform("io.arrow-kt:arrow-stack:1.2.1"))
  implementation("io.arrow-kt:arrow-core")
  implementation("io.arrow-kt:arrow-fx-coroutines")
  implementation("io.arrow-kt:arrow-optics")
  //kapt("io.arrow-kt:arrow-meta:1.6.2")

  // tests
  testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
  testImplementation("io.kotest:kotest-assertions-core:5.7.2")
  testImplementation("io.kotest:kotest-assertions-json:5.7.2")
  testImplementation("io.kotest:kotest-property:5.7.2")
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
  // TODO (verify) this should allow to pass any "-Dconfig.key=value" property
  systemProperties(System.getProperties().mapKeys { it.key.toString() }.toMap())
}

task("runReactorExample", JavaExec::class) {
  mainClass.set("com.github.niqdev.reactor.ReactorExampleKt")
  classpath = sourceSets["main"].runtimeClasspath
}
task("runCliktExample", JavaExec::class) {
  mainClass.set("com.github.niqdev.clikt.CliktExampleKt")
  classpath = sourceSets["main"].runtimeClasspath
  args = listOf((project.properties.getOrElse("myArgs", { "--help" }) as String))
}
task("runJsonExample", JavaExec::class) {
  mainClass.set("com.github.niqdev.serialization.JsonExampleKt")
  classpath = sourceSets["main"].runtimeClasspath
}
task("runCoroutineComparison", JavaExec::class) {
  mainClass.set("com.github.niqdev.coroutine.CoroutineComparisonKt")
  classpath = sourceSets["main"].runtimeClasspath

  // adds extra info in logs
  jvmArgs = listOf("-Dkotlinx.coroutines.debug")
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
