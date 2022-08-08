import org.gradle.api.Plugin
import org.gradle.api.Project

// https://docs.gradle.org/current/userguide/custom_plugins.html
class GreetingPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.task("hello") {
      doLast {
        println(">>>>>>>>>> Hello from the GreetingPlugin")
      }
    }
  }
}
