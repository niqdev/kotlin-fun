plugins {
  alias(libs.plugins.test.logger)
  alias(libs.plugins.shadow)
  application
}

dependencies {
  // kotlin
  implementation(platform(libs.kotlin.bom))
  implementation(libs.kotlin.stdlib.jdk8)

  implementation(libs.slf4j.api)
  runtimeOnly(libs.logback.classic)

  // aws
  implementation(platform(libs.aws.v2.bom))
  implementation(libs.aws.v2.lambda)
  implementation(libs.aws.v2.s3)
  implementation(libs.aws.v2.sqs)
  implementation(libs.aws.lambda.core)
  implementation(libs.aws.lambda.events)

  // tests
  testImplementation(libs.kotest.runner.junit5)
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
