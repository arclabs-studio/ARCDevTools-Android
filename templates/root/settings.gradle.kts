// ARCDevTools-Android — root settings template
// Version: 0.5.0
//
// Installed by arc-setup only when the project has no settings.gradle.kts yet.

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // gradle/gradle-daemon-jvm.properties pins the Gradle daemon to JDK 21 (detekt 1.23.8 cannot
    // run on JDK 25, the JBR bundled with current Android Studio). This plugin lets Gradle
    // download that JDK when none is installed; regenerate the file with
    // `./gradlew updateDaemonJvm --jvm-version=21`. The version catalog is not available in
    // this block, so the plugin version is declared here.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "__PROJECT_NAME__"

include(":app")
