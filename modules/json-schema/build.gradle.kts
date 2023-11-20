plugins {
  alias(libs.plugins.test.logger)
}

repositories {
  maven("https://packages.confluent.io/maven/")
}

dependencies {
  // json schema
  implementation(libs.everit.json.schema)
  implementation(libs.json.kotlin.schema)

  // json-schema compatibility
  implementation(libs.kafka.schema.provider)
  implementation(libs.kafka.schema.registry)
  testImplementation(libs.kotest.kafka) // deprecated, see testcontainers

  // json diff
  implementation(libs.zjsonpatch)
  implementation(libs.jackson.module)

  // testing
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true
}
