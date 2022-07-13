plugins {
  id("com.adarshr.test-logger") version Versions.testLogger
}

version = "0.1.0"

dependencies {
  implementation("com.amazonaws:aws-lambda-java-core:${Versions.awsLambdaCore}")
  implementation("com.amazonaws:aws-lambda-java-events:${Versions.awsLambdaEvents}")
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
