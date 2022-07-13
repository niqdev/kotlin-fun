plugins {
  id("com.adarshr.test-logger") version Versions.testLogger
  application
}

group = "com.github.niqdev.aws.serverless"
version = "0.1.0"

dependencies {
  implementation("com.amazonaws:aws-lambda-java-core:${Versions.awsLambdaCore}")
  implementation("com.amazonaws:aws-lambda-java-events:${Versions.awsLambdaEvents}")
  runtimeOnly("com.amazonaws:aws-lambda-java-log4j2:1.5.1")
}

// TODO ???
application {
  mainClass.set("com.github.niqdev.aws.serverless.Handler")
}

// TODO https://docs.aws.amazon.com/lambda/latest/dg/java-package.html
tasks {
  register<Zip>("buildZip") {
    archiveFileName.set("${project.name}-${project.version}.zip")
    from(compileKotlin)
    from(processResources)
    // resolves in the step below "Entry META-INF/MANIFEST.MF is a duplicate but no duplicate handling strategy has been set"
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    /*
    // unpack all the jars in the classes together in the root directory
    from(
      configurations.runtimeClasspath.get().filter {
        it.name.endsWith("jar")
      }.map {
        zipTree(it)
      }
    )
    */
    into("lib") {
      from(configurations.runtimeClasspath)
    }
  }

  build {
    finalizedBy("buildZip")
  }
}
