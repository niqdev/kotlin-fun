plugins {
  kotlin("jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
  id("com.github.johnrengelman.shadow") version Versions.shadow
  application
}

repositories {
  mavenCentral()
}

dependencies {
  // kotlin
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
  runtimeOnly("ch.qos.logback:logback-classic:${Versions.logback}")

  // aws
  implementation(platform("software.amazon.awssdk:bom:${Versions.aws}"))
  implementation("software.amazon.awssdk:lambda")
  implementation("software.amazon.awssdk:s3")
  implementation("software.amazon.awssdk:sqs")
  implementation("com.amazonaws:aws-lambda-java-core:${Versions.awsLambdaCore}")
  implementation("com.amazonaws:aws-lambda-java-events:${Versions.awsLambdaEvents}")

  // tests
  testImplementation("io.kotest:kotest-runner-junit5:${Versions.kotest}")
}

application {
  val name = "com.github.niqdev.HandlerKt"
  mainClass.set(name)
  // required by ShadowJar
  mainClassName = name
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
  useJUnitPlatform()
}
