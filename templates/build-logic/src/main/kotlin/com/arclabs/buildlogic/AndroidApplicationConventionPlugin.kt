package com.arclabs.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * `arc.android.application` — an Android app module.
 *
 * compileSdk 37 · targetSdk 36 · minSdk 26 · Java/Kotlin 17 · Kotlin warnings are errors · Lint
 * warnings are errors · JUnit 5 unit tests · JaCoCo line coverage >= `arc.coverage.minimum`
 * (default 80%) on `check` · detekt + ktlint.
 * targetSdk 36 makes edge-to-edge mandatory: the app calls `enableEdgeToEdge()` (activity-compose,
 * added by `arc.android.compose`) and handles insets.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("arc.android.lint")

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = libs.versionInt("targetSdk")
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                buildTypes.getByName("debug").enableUnitTestCoverage = true
            }
            configureAndroidUnitTests()
            configureAndroidCoverage(extensions.getByType<ApplicationAndroidComponentsExtension>())
            configureStaticAnalysis()

            dependencies {
                add("implementation", libs.library("androidx-core-ktx"))
            }
        }
    }
}
