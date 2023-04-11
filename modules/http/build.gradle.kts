plugins {
  id("com.adarshr.test-logger") version Versions.testLogger

  application
  // TODO is this used?
  id("io.ktor.plugin") version Versions.ktor
}

dependencies {
  // server
  implementation("io.ktor:ktor-server-core-jvm:${Versions.ktor}")
  implementation("io.ktor:ktor-server-netty-jvm:${Versions.ktor}")

  // logging
  implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
  runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")

  // testing
  testImplementation("io.ktor:ktor-server-tests-jvm:${Versions.ktor}")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}")
}

application {
  mainClass.set("com.github.niqdev.ktor.server.ApplicationKt")

  applicationDefaultJvmArgs = listOf(
    "-Dio.ktor.development",
    "-Dkotlinx.coroutines.debug"
  )
}
