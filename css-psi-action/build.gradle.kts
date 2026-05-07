import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
}

dependencies {
    testImplementation(libs.junit)
    intellijPlatform {
        intellijIdea("2025.3.4.1")
        testFramework(TestFrameworkType.Platform)
    }
}
