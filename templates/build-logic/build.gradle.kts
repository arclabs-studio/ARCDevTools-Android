// ARCDevTools-Android — build-logic
// Version: 0.4.0
//
// Convention plugins shared by every ARC Labs Android project. The third-party Gradle plugins
// are compileOnly here: the root build.gradle.kts puts them on the build classpath with
// `apply false`, so their versions come from the version catalog only.

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.arclabs.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        allWarningsAsErrors = true
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room3.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
    compileOnly(libs.ktlint.gradlePlugin)
    compileOnly(libs.android.junit5.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.arc.android.application.get().pluginId
            implementationClass = "com.arclabs.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.arc.android.library.get().pluginId
            implementationClass = "com.arclabs.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = libs.plugins.arc.android.feature.get().pluginId
            implementationClass = "com.arclabs.buildlogic.AndroidFeatureConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.arc.android.compose.get().pluginId
            implementationClass = "com.arclabs.buildlogic.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = libs.plugins.arc.android.hilt.get().pluginId
            implementationClass = "com.arclabs.buildlogic.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = libs.plugins.arc.android.room.get().pluginId
            implementationClass = "com.arclabs.buildlogic.AndroidRoomConventionPlugin"
        }
        register("androidLint") {
            id = libs.plugins.arc.android.lint.get().pluginId
            implementationClass = "com.arclabs.buildlogic.AndroidLintConventionPlugin"
        }
        register("jvmLibrary") {
            id = libs.plugins.arc.jvm.library.get().pluginId
            implementationClass = "com.arclabs.buildlogic.JvmLibraryConventionPlugin"
        }
    }
}
