plugins {
  id("org.jetbrains.kotlin.jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
  id("com.adarshr.test-logger") version Versions.testLogger
}

repositories {
  mavenCentral()
}

dependencies {
  testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
  testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
}

tasks.withType<Test> {
  // required for kotest
  useJUnitPlatform()
  // print to stdout
  testLogging.showStandardStreams = true
}

task("runLox", JavaExec::class) {
  mainClass.set("com.github.niqdev.Lox")
  classpath = sourceSets["main"].runtimeClasspath

  //systemProperties = System.getProperties().toMap().mapKeys { k -> k as String }
}
