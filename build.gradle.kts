plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlinter)
}

// configurations shared with all the modules, except `buildSrc`
allprojects {

  // without `apply` the sub-modules they won't see the plugins
  apply(plugin = "org.jetbrains.kotlin.jvm")
  apply(plugin = "org.jmailen.kotlinter")

  repositories {
    mavenLocal()
    mavenCentral()
  }

  kotlin {
    jvmToolchain {
      languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
    }
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      // fails compilation if there is a warning e.g. Deprecated
      //allWarningsAsErrors = true
    }
  }
}
