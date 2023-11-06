plugins {
  alias(libs.plugins.test.logger)
}

version = "0.1.0"

dependencies {
  implementation("com.amazonaws:aws-lambda-java-core:1.2.3")
  implementation("com.amazonaws:aws-lambda-java-events:3.11.3")

  // https://docs.aws.amazon.com/lambda/latest/dg/java-logging.html#java-logging-log4j2
  implementation("org.apache.logging.log4j:log4j-api:2.19.0")
  implementation("org.apache.logging.log4j:log4j-core:2.19.0")
  // FIXME aws lambda runtimeOnly vs compileOnly https://stackoverflow.com/questions/30731084/provided-dependency-in-gradle
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.21.1")
  runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.1")
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
