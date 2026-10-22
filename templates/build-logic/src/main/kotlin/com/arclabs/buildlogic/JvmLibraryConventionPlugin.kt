package com.arclabs.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * `arc.jvm.library` — a pure Kotlin/JVM module (e.g. `domain`): no Android dependencies.
 * Java/Kotlin 17 · Kotlin warnings as errors · Android Lint (standalone) · JUnit 5 ·
 * JaCoCo line coverage >= `arc.coverage.minimum` (default 80%) on `check` · detekt + ktlint.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("arc.android.lint")

            configureKotlinJvm()
            configureJvmUnitTests()
            configureJvmCoverage()
            configureStaticAnalysis()
        }
    }
}
