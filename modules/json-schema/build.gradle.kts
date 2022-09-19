plugins {
  id("com.adarshr.test-logger") version Versions.testLogger
}

repositories {
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  // json schema
  implementation("com.github.erosb:everit-json-schema:${Versions.everit}")
  implementation("net.pwall.json:json-kotlin-schema:${Versions.pwall}")

  // json-schema compatibility
  implementation("io.confluent:kafka-json-schema-provider:${Versions.confluent}")
  testImplementation("io.confluent:kafka-schema-registry:${Versions.confluent}")
  testImplementation("io.kotest.extensions:kotest-extensions-embedded-kafka:${Versions.kotestKafka}")

  // testing
  testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
  testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true
}
