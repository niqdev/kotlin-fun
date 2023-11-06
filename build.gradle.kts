plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.kotlinter)
}

// configurations shared with all the modules, except `buildSrc`
allprojects {

  // without `apply` the sub-modules won't see the plugins
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jmailen.kotlinter")

  repositories {
    mavenLocal()
    mavenCentral()
  }

  tasks.withType<JavaCompile> {
    targetCompatibility = JavaVersion.VERSION_17.majorVersion
    sourceCompatibility = JavaVersion.VERSION_17.majorVersion
  }
  // fixes: 'compileJava' task (current target is 11) and 'compileKotlin' task (current target is 1.8) jvm target compatibility should be set to the same Java version
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = JavaVersion.VERSION_17.majorVersion
      // fails compilation if there is a warning e.g. Deprecated
      //allWarningsAsErrors = true
    }
  }
}
