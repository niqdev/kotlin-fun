dependencies {
  implementation(kotlin("script-runtime"))

  implementation(libs.jdom)
}

// ------------------------------

// https://stackoverflow.com/questions/18421857/is-it-possible-to-specify-multiple-main-classes-using-gradle-application-plugi
// https://stackoverflow.com/questions/57875930/gradle-application-plugin-with-kotlin-dsl-with-multiple-main-classes

task("runMyStream", JavaExec::class) {
  mainClass.set("com.github.niqdev.MyStreamKt")
  classpath = sourceSets["main"].runtimeClasspath
}

task("runMyList", JavaExec::class) {
  mainClass.set("com.github.niqdev.MyListKt")
  classpath = sourceSets["main"].runtimeClasspath
}
