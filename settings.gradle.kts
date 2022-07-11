rootProject.name = "kotlin-fun"

include(
  "modules:app",
  "modules:aws-local",
  "modules:aws-sdk-serverless",
  "modules:bool",
  "modules:fpk",
  "modules:jok",
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
