// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version libs.versions.hilt.get() apply false

    id("org.sonarqube") version "4.0.0.2929"

}

sonarqube {
    properties {
        property("sonar.projectKey", "your-project-key")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.login", "your-sonarqube-token")
    }
}

dependencies {
    // other plugins...
}
