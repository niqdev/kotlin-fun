plugins {
  alias(libs.plugins.test.logger)
}

version = "0.1.0"

dependencies {
  implementation(libs.aws.lambda.core)
  implementation(libs.aws.lambda.events)

  // https://docs.aws.amazon.com/lambda/latest/dg/java-logging.html#java-logging-log4j2
  implementation(libs.log4j.api)
  implementation(libs.log4j.core)
  // FIXME aws lambda runtimeOnly vs compileOnly https://stackoverflow.com/questions/30731084/provided-dependency-in-gradle
  implementation(libs.log4j.slf4j)
  runtimeOnly(libs.aws.lambda.log4j2)
}

// https://docs.aws.amazon.com/lambda/latest/dg/java-package.html
tasks {
  register<Zip>("buildZip") {
    archiveFileName.set("${project.name}-${project.version}.zip")
    from(compileKotlin)
    from(processResources)
    into("lib") {
      from(configurations.runtimeClasspath)
    }
  }
  build {
    dependsOn("buildZip")
  }
}
