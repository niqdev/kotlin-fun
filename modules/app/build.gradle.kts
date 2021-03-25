plugins {
  application
  id("org.jetbrains.kotlin.jvm") version "1.4.21-2"
  id("org.jmailen.kotlinter") version "3.4.0"
}

repositories {
  jcenter()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("ch.qos.logback:logback-classic:1.2.3")

  // tests
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  mainClass.set("com.github.niqdev.AppKt")
}
