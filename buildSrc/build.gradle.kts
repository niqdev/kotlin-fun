plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
}

// alternative to kotlin.jvmToolchain
tasks.withType<JavaCompile> {
  targetCompatibility = JavaVersion.VERSION_17.majorVersion
  sourceCompatibility = JavaVersion.VERSION_17.majorVersion
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_17.majorVersion
  }
}
