plugins {
  alias(libs.plugins.test.logger)

  application
  alias(libs.plugins.ktor)
}

dependencies {
  // http
  implementation(libs.bundles.ktor.server)
  implementation(libs.ktor.jackson)

  // logging
  implementation(libs.slf4j.api)
  runtimeOnly(libs.logback.classic)

  // testing
  testImplementation(libs.ktor.server.tests)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.mockk)
}

application {
  mainClass.set("com.github.niqdev.ktor.server.ApplicationKt")

  applicationDefaultJvmArgs = listOf(
    "-Dio.ktor.development",
    "-Dkotlinx.coroutines.debug"
  )
}
