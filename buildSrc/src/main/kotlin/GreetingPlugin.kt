import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

// https://docs.gradle.org/current/userguide/custom_plugins.html
class GreetingPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.task("hello") {
      doLast {
        println(">>>>>>>>>> Hello from the GreetingPlugin")
        println("ls -la".runCommand(File(".")))
      }
    }
  }

  // https://stackoverflow.com/questions/35421699/how-to-invoke-external-command-from-within-kotlin-code
  private fun String.runCommand(workingDir: java.io.File): String? {
    try {
      val parts = this.split("\\s".toRegex())
      val proc = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

      proc.waitFor(60, java.util.concurrent.TimeUnit.MINUTES)
      return proc.inputStream.bufferedReader().readText()
    } catch(e: java.io.IOException) {
      e.printStackTrace()
      return null
    }
  }
}
