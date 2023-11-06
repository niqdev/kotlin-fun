plugins {
  alias(libs.plugins.test.logger)
}

repositories {
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  // json schema
  implementation("com.github.erosb:everit-json-schema:1.14.2")
  implementation("net.pwall.json:json-kotlin-schema:0.42")

  // json-schema compatibility
  implementation("io.confluent:kafka-json-schema-provider:7.3.3") // FIXME 7.4.0
  testImplementation("io.confluent:kafka-schema-registry:7.3.3")
  testImplementation("io.kotest.extensions:kotest-extensions-embedded-kafka:2.0.0") // deprecated, see testcontainers

  // json diff
  implementation("com.flipkart.zjsonpatch:zjsonpatch:0.4.14")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

  // testing
  testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
  testImplementation("io.kotest:kotest-assertions-core:5.7.2")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true
}
