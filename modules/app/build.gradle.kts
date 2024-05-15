plugins {
  alias(libs.plugins.test.logger)
  alias(libs.plugins.kotlin.serialization)

  application
  // https://docs.gradle.org/current/userguide/idea_plugin.html
  idea
}

dependencies {
  implementation(platform(libs.kotlin.bom))
  implementation(libs.kotlinx.coroutines)
  implementation(libs.kotlinx.serialization)

  // logging
  implementation(libs.slf4j.api)
  runtimeOnly(libs.logback.classic)

  // config
  implementation(libs.bundles.hoplite)

  // json
  implementation(libs.kondor.core)
  testImplementation(libs.kondor.tools)

  // other
  implementation(libs.clikt)
  implementation(libs.uuid)

  // reactor
  implementation(libs.reactor.core)
  implementation(libs.reactor.kotlin)

  // arrow
  implementation(platform(libs.arrow.stack))
  implementation(libs.arrow.core)

  // tests
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
  testImplementation(libs.kotest.assertions.json)
  testImplementation(libs.kotest.property)
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
