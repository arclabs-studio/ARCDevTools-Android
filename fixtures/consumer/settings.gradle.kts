// ARCDevTools-Android consumer fixture — settings
//
// Committed on purpose: arc-setup keeps an existing settings.gradle.kts, so this file declares the
// fixture's modules while every other Gradle file (version catalog, build-logic, root build file,
// gradle.properties, daemon JVM criteria) is generated from templates/ by arc-setup. The
// pluginManagement and dependencyResolutionManagement blocks mirror templates/root/settings.gradle.kts.

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
    // Lets Gradle download the JDK 21 that gradle/gradle-daemon-jvm.properties asks for.
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

rootProject.name = "arc-consumer-fixture"

include(":app", ":domain", ":core:ui", ":feature:sample")
