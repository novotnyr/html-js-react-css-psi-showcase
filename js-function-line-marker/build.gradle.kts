import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    intellijPlatform {
        intellijIdea("2025.3.4.1")
        testFramework(TestFrameworkType.Platform)
    }
}
