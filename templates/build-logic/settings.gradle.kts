// ARCDevTools-Android — build-logic settings
// Version: 0.5.1
//
// Included build that hosts the arc.* convention plugins. It reads the consumer project's
// version catalog so plugin versions stay in one place (gradle/libs.versions.toml).

dependencyResolutionManagement {
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
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
