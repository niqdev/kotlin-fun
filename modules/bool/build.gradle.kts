plugins {
  id("com.adarshr.test-logger") version Versions.testLogger
}

dependencies {
  testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
  testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true
}
