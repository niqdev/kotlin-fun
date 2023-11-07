dependencies {
  implementation(libs.aws.kotlin.lambda)

  implementation(libs.slf4j.api)
  runtimeOnly(libs.logback.classic)
}
