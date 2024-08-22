rootProject.name = "kotlin-fun"

include(
  "modules:app",
  "modules:aws-kotlin",
  "modules:aws-local",
  "modules:aws-serverless",
  "modules:bool",
  "modules:fpk",
  "modules:http",
  // "modules:jok", // TODO fix compilation errors
  "modules:json-schema",
  "modules:kia",
  "modules:lox",
  // "modules:rekursive" // TODO fix compilation errors
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
