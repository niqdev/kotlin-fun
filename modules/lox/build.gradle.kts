plugins {
  id("com.adarshr.test-logger") version Versions.testLogger
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
}
