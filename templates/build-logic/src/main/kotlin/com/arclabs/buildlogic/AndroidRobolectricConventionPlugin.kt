package com.arclabs.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

/**
 * `arc.android.robolectric` — Robolectric local tests (JUnit 4 runner on the JUnit Vintage engine)
 * for an Android module. Apply after `arc.android.application` or `arc.android.library`.
 * `arc.android.compose` and `arc.android.room` apply it themselves.
 */
class AndroidRobolectricConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureRobolectricTests(extensions.getByType<CommonExtension>())
        }
    }
}
