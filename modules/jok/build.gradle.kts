plugins {
  id("org.jetbrains.kotlin.jvm") version Versions.kotlin
  id("org.jmailen.kotlinter") version Versions.kotlinter
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("script-runtime"))
}

// ------------------------------

// https://stackoverflow.com/questions/18421857/is-it-possible-to-specify-multiple-main-classes-using-gradle-application-plugi
// https://stackoverflow.com/questions/57875930/gradle-application-plugin-with-kotlin-dsl-with-multiple-main-classes

task("runMyStream", JavaExec::class) {
  main = "com.github.niqdev.MyStreamKt"
  classpath = sourceSets["main"].runtimeClasspath
}

task("runMyList", JavaExec::class) {
  main = "com.github.niqdev.MyListKt"
  classpath = sourceSets["main"].runtimeClasspath
}
