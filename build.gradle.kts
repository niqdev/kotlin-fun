plugins {
  kotlin("jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
}

allprojects {

  // avoid to re-define them in all the sub-modules: without `apply` the sub-modules won't see the plugins
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jmailen.kotlinter")

  repositories {
    mavenLocal()
    mavenCentral()
  }

  // fixes: 'compileJava' task (current target is 11) and 'compileKotlin' task (current target is 1.8) jvm target compatibility should be set to the same Java version
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "11"
      //allWarningsAsErrors = true
    }
  }
  tasks.withType<JavaCompile> {
    targetCompatibility = "11"
    sourceCompatibility = "11"
  }
}
