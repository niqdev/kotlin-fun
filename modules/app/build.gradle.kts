plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.20"

    // application plugin
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // tests
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    mainClass.set("com.github.niqdev.AppKt")
}
