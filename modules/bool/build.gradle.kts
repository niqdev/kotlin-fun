plugins {
  id("org.jetbrains.kotlin.jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
  id("com.adarshr.test-logger") version Versions.testLogger
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
  testImplementation("io.kotest:kotest-assertions-core:${Versions.kotest}")
}

tasks.withType<Test> {
  // required for kotest
  useJUnitPlatform()
  // print to stdout
  testLogging.showStandardStreams = true
}

// fixes: 'compileJava' task (current target is 11) and 'compileKotlin' task (current target is 1.8) jvm target compatibility should be set to the same Java version
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "11"
  }
}
