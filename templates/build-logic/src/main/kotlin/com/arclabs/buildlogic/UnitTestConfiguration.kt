package com.arclabs.buildlogic

import de.mannodermaus.gradle.plugins.junit5.dsl.AndroidJUnitPlatformExtension
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

/**
 * JUnit 5 (Jupiter) on the JUnit Platform for local unit tests of an Android module.
 * The mannodermaus `de.mannodermaus.android-junit` plugin switches every unit test task to the
 * JUnit Platform. Turbine and kotlinx-coroutines-test are the default async test tools.
 * The plugin's own JaCoCo integration is disabled: coverage comes from AGP's native support
 * (see CoverageConfiguration.kt).
 */
internal fun Project.configureAndroidUnitTests() {
    pluginManager.apply("de.mannodermaus.android-junit")
    extensions.configure<AndroidJUnitPlatformExtension> {
        jacocoOptions.taskGenerationEnabled.set(false)
    }
    addJUnit5Dependencies()
}

/** JUnit 5 (Jupiter) on the JUnit Platform for a pure Kotlin/JVM module. */
internal fun Project.configureJvmUnitTests() {
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
    addJUnit5Dependencies()
}

private fun Project.addJUnit5Dependencies() {
    dependencies {
        add("testImplementation", platform(libs.library("junit-bom")))
        add("testImplementation", libs.library("junit-jupiter-api"))
        add("testImplementation", libs.library("junit-jupiter-params"))
        add("testImplementation", libs.library("kotlinx-coroutines-test"))
        add("testImplementation", libs.library("turbine"))
        add("testRuntimeOnly", libs.library("junit-jupiter-engine"))
        add("testRuntimeOnly", libs.library("junit-platform-launcher"))
    }
}
