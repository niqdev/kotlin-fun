plugins {
  alias(libs.plugins.test.logger)

  application
  alias(libs.plugins.ktor)
}

dependencies {
  // server
  implementation("io.ktor:ktor-server-core-jvm:2.3.4")
  implementation("io.ktor:ktor-server-netty-jvm:2.3.4")

  // logging
  implementation("org.slf4j:slf4j-api:2.0.9")
  runtimeOnly("ch.qos.logback:logback-classic:1.4.11")

  // testing
  testImplementation("io.ktor:ktor-server-tests-jvm:2.3.4")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.20")
}

application {
  mainClass.set("com.github.niqdev.ktor.server.ApplicationKt")

  applicationDefaultJvmArgs = listOf(
    "-Dio.ktor.development",
    "-Dkotlinx.coroutines.debug"
  )
}
