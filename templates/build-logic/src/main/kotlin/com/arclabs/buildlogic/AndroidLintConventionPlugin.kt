package com.arclabs.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.Lint
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * `arc.android.lint` — Android Lint with zero tolerance: every warning is an error and the
 * build aborts. Applied automatically by the application, library and JVM conventions; on a
 * pure JVM module it applies the standalone `com.android.lint` plugin.
 */
class AndroidLintConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            when {
                pluginManager.hasPlugin("com.android.application") ->
                    extensions.configure<ApplicationExtension> { lint { configureArcLint() } }

                pluginManager.hasPlugin("com.android.library") ->
                    extensions.configure<LibraryExtension> { lint { configureArcLint() } }

                else -> {
                    pluginManager.apply("com.android.lint")
                    extensions.configure<Lint> { configureArcLint() }
                }
            }
        }
    }
}

/**
 * Checks that compare declared versions with what is newest upstream. Versions and SDK levels
 * are pinned on purpose in gradle/libs.versions.toml and bumped deliberately; with
 * warningsAsErrors these checks would break an unchanged build whenever a library or SDK ships.
 */
private val VERSION_FRESHNESS_CHECKS = listOf(
    "AndroidGradlePluginVersion",
    "GradleDependency",
    "NewerVersionAvailable",
    "OldTargetApi",
)

private fun Lint.configureArcLint() {
    warningsAsErrors = true
    abortOnError = true
    checkReleaseBuilds = true
    checkDependencies = false
    disable += VERSION_FRESHNESS_CHECKS
}
