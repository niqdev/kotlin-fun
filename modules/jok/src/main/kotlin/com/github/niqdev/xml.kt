package com.github.niqdev

// https://mkyong.com/java/how-to-read-xml-file-in-java-jdom-example
object XmlImperative {
  fun run() {
    val builder = org.jdom2.input.SAXBuilder()
    val xmlFile = java.io.File("modules/jok/src/main/resources/example.xml")
    try {
      val document = builder.build(xmlFile)
      val rootNode = document.rootElement
      val list = rootNode.getChildren("staff")
      list.forEach {
        println("First Name: ${it.getChildText("firstName")}")
        println("Last Name: ${it.getChildText("lastName")}")
        println("Email: ${it.getChildText("email")}")
        println("Salary: ${it.getChildText("salary")}")
      }
    } catch (io: java.io.IOException) {
      println(io.message)
    } catch (e: org.jdom2.JDOMException) {
      println(e.message)
    }
  }
}

// fp example
// https://github.com/pysaumont/fpinkotlin/blob/master/fpinkotlin-parent/fpinkotlin-commonproblems/src/main/kotlin/com/fpinkotlin/commonproblems/xml/step7/ReadXmlFile.kt
fun main() {
  XmlImperative.run()
}
