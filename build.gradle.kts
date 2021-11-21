plugins {
  kotlin("jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
}

// config shared with all the modules
allprojects {

  // without `apply` the sub-modules won't see the plugins
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jmailen.kotlinter")

  repositories {
    mavenLocal()
    mavenCentral()
  }

  tasks.withType<JavaCompile> {
    targetCompatibility = JavaVersion.VERSION_11.majorVersion
    sourceCompatibility = JavaVersion.VERSION_11.majorVersion
  }
  // fixes: 'compileJava' task (current target is 11) and 'compileKotlin' task (current target is 1.8) jvm target compatibility should be set to the same Java version
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_11.majorVersion
      // fails compilation if there is a warning e.g. Deprecated
      //allWarningsAsErrors = true
    }
  }
}
