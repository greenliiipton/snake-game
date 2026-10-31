import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("java")
    id("org.jetbrains.changelog")
    id("org.jetbrains.intellij.platform")
}

group = "com.greenliiipton"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

dependencies {
    testImplementation(libs.junit)

    intellijPlatform {
        // Build and run the plugin against the exact PyCharm version installed by the team.
        local("/Applications/PyCharm.app")
        testFramework(TestFrameworkType.Platform)
    }
}
