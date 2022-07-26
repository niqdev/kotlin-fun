plugins {
  id("com.adarshr.test-logger") version Versions.testLogger
}

dependencies {
  implementation("com.github.erosb:everit-json-schema:${Versions.everit}")

  implementation("com.ubertob.kondor:kondor-core:${Versions.kondor}")
  testImplementation("com.ubertob.kondor:kondor-tools:${Versions.kondor}")

  testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
  testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true
}
