plugins {
  alias(libs.plugins.test.logger)
  id("com.github.johnrengelman.shadow") version "8.1.1"
  application
}

dependencies {
  // kotlin
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("org.slf4j:slf4j-api:2.0.9")
  runtimeOnly("ch.qos.logback:logback-classic:1.4.11")

  // aws
  implementation(platform("software.amazon.awssdk:bom:2.20.153"))
  implementation("software.amazon.awssdk:lambda")
  implementation("software.amazon.awssdk:s3")
  implementation("software.amazon.awssdk:sqs")
  implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
  implementation("com.amazonaws:aws-lambda-java-events:3.11.3")

  // tests
  testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
}

application {
  val name = "com.github.niqdev.HandlerKt"
  mainClass.set(name)
  // required by ShadowJar (deprecated)
  //mainClassName = name
}

tasks.withType<Test> {
  useJUnitPlatform()
}
