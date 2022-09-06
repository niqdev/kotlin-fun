rootProject.name = "kotlin-fun"

include(
  "modules:app",
  "modules:aws-kotlin",
  "modules:aws-local",
  "modules:aws-serverless",
  "modules:aws-serverless-sqs",
  "modules:bool",
  "modules:fpk",
  "modules:jok",
  "modules:json-schema",
  "modules:kia",
  "modules:lox",
  "modules:rekursive"
)

// example of how to include a project for development, without publishing to mavenLocal
if (file("../PROJECT_PATH").exists()) {
  includeBuild("../PROJECT_PATH") {
    dependencySubstitution {
      // replaces library import in `build.gradle.kts` with a project module
      substitute(module("GROUP_ID:ARTIFACT_ID")).using(project(":MODULE_NAME"))
    }
  }
}
