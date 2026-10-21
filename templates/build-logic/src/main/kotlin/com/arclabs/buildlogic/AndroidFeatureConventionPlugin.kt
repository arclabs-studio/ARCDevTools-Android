package com.arclabs.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * `arc.android.feature` — a `feature:*` module: an Android library with Compose and Hilt,
 * Navigation 3 (serializable `NavKey`s, ViewModel scoped per back-stack entry) and
 * lifecycle-aware ViewModels. Features never depend on other features.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("arc.android.library")
            pluginManager.apply("arc.android.compose")
            pluginManager.apply("arc.android.hilt")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            dependencies {
                add("implementation", libs.library("androidx-navigation3-runtime"))
                add("implementation", libs.library("androidx-lifecycle-viewmodel-compose"))
                add("implementation", libs.library("androidx-lifecycle-viewmodel-navigation3"))
                add("implementation", libs.library("kotlinx-serialization-json"))
            }
        }
    }
}
