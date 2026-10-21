package com.arclabs.buildlogic

import androidx.room3.gradle.RoomExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * `arc.android.room` — Room 3 (`androidx.room3`, KSP only) with exported schemas in
 * `<module>/schemas` so migrations can be tested. Apply after `arc.android.library`.
 */
class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("androidx.room3")

            extensions.configure<RoomExtension> {
                schemaDirectory(layout.projectDirectory.dir("schemas").asFile.path)
            }

            dependencies {
                add("implementation", libs.library("androidx-room3-runtime"))
                add("ksp", libs.library("androidx-room3-compiler"))
                add("testImplementation", libs.library("androidx-room3-testing"))
            }
        }
    }
}
