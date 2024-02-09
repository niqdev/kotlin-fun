plugins {
  alias(libs.plugins.test.logger)
}

dependencies {
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)

  implementation(libs.jackson.module)
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true
}
