plugins {
  alias(libs.plugins.test.logger)

  application
  alias(libs.plugins.ktor)
}

dependencies {
  // http
  implementation(platform(libs.ktor.bom))
  implementation(libs.bundles.ktor.server)
  implementation(libs.bundles.ktor.client)
  implementation(libs.ktor.jackson)
  implementation(libs.jackson.module)

  // database
  implementation(platform(libs.jdbi.bom))
  implementation(libs.jdbi.core)
  implementation(libs.jdbi.postgres)
  implementation(libs.flyway.core)
  implementation(libs.flyway.postgres)
  implementation(libs.driver.postgres)

  // logging
  implementation(libs.kotlin.logging)
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
