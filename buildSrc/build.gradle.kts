plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
}

tasks.withType<JavaCompile> {
  targetCompatibility = JavaVersion.VERSION_11.majorVersion
  sourceCompatibility = JavaVersion.VERSION_11.majorVersion
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_11.majorVersion
  }
}
