plugins {
  alias(libs.plugins.test.logger)
}

dependencies {
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)

  implementation(platform(libs.jackson.bom))
  implementation(libs.bundles.jackson)
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging.showStandardStreams = true
}
