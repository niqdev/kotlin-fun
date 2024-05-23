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

  // json
  implementation(platform(libs.jackson.bom))
  implementation(libs.bundles.jackson)

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

  // config
  implementation(libs.bundles.hoplite)

  // testing
  implementation(platform(libs.junit.bom))
  testImplementation(libs.bundles.junit)

  implementation(platform(libs.testcontainers.bom))
  testImplementation(libs.bundles.testcontainers)

  testImplementation(libs.ktor.server.tests)
  testImplementation(libs.mockk)
  testImplementation(libs.kotest.property)

  /*
  implementation("<GROUP>:<MODULE>:<VERSION>") {
    exclude(group = "my-group", module = "my-module")
  }
  implementation("<GROUP>:<MODULE>:<VERSION>") {
    because("FIX DEPENDENCIES")
  }
  */
}

application {
  mainClass.set("com.github.niqdev.ktor.server.ApplicationKt")

  applicationDefaultJvmArgs = listOf(
    "-Dio.ktor.development",
    "-Dkotlinx.coroutines.debug"
  )
}

task("run-client", JavaExec::class) {
  mainClass.set("com.github.niqdev.ktor.client.ApplicationKt")
  classpath = sourceSets["main"].runtimeClasspath
}

// required for junit and kotest - not for kotlin-test-junit
tasks.withType<Test> {
  useJUnitPlatform()
}

ktor {
  val repository = "${rootProject.name}-${project.name}"
  docker {
    localImageName.set("niqdev/$repository")
    imageTag.set(System.getenv("GITHUB_REF_NAME") ?: "dev")

    // https://hub.docker.com/repository/docker/niqdev/kotlin-fun-http
    // https://github.com/niqdev/kotlin-fun/settings/secrets/actions > Repository secrets
    externalRegistry.set(
      io.ktor.plugin.features.DockerImageRegistry.dockerHub(
        appName = providers.provider { repository },
        username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
        password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
      )
    )
  }
}
