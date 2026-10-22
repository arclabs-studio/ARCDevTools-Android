package com.arclabs.buildlogic

import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * `arc.android.library` — an Android library module (`core:*`).
 *
 * Same baseline as `arc.android.application`: SDK 37/36/26 (compile/target/min), Java/Kotlin 17,
 * Kotlin and Lint warnings as errors, JUnit 5, JaCoCo line coverage >= `arc.coverage.minimum`
 * (default 80%) on `check`, detekt + ktlint.
 */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("arc.android.lint")

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                testOptions.targetSdk = libs.versionInt("targetSdk")
                lint.targetSdk = libs.versionInt("targetSdk")
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                buildTypes.getByName("debug").enableUnitTestCoverage = true
            }
            configureAndroidUnitTests()
            configureAndroidCoverage(extensions.getByType<LibraryAndroidComponentsExtension>())
            configureStaticAnalysis()
        }
    }
}
