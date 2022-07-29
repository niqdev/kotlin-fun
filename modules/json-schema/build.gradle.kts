plugins {
  id("com.adarshr.test-logger") version Versions.testLogger
}

dependencies {
  // json schema
  implementation("com.github.erosb:everit-json-schema:${Versions.everit}")
  implementation("net.pwall.json:json-kotlin-schema:${Versions.pwall}")

  // TODO json
  implementation("com.ubertob.kondor:kondor-core:${Versions.kondor}")
  testImplementation("com.ubertob.kondor:kondor-tools:${Versions.kondor}")

  // testing
  testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
  testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true
}
