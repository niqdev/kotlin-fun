plugins {
  alias(libs.plugins.test.logger)
}

dependencies {
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
}

tasks.withType<Test> {
  // required for kotest
  useJUnitPlatform()
  // print to stdout
  testLogging.showStandardStreams = true
}
