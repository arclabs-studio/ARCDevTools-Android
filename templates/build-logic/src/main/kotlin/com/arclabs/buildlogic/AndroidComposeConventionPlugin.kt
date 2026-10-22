package com.arclabs.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

/**
 * `arc.android.compose` — Jetpack Compose on top of `arc.android.application` or
 * `arc.android.library` (apply one of those first).
 *
 * Compose compiler plugin · Compose BOM + Material 3 (Expressive) · activity-compose and
 * lifecycle-runtime-compose · Robolectric + Compose UI tests (JUnit 4 rule run by the JUnit
 * Vintage engine) · Compose Preview Screenshot Testing (`screenshotTest` source set,
 * `validateDebugScreenshotTest`) · detekt Compose rules.
 *
 * Screenshot testing uses the standalone `com.android.compose.screenshot` plugin, the setup
 * documented for AGP < 9.5.0-alpha03. Migrate to AGP test suites when AGP is upgraded.
 * A Compose module without a `src/screenshotTest` source set (e.g. `app`) still gets the
 * `validate<Variant>ScreenshotTest` / `update<Variant>ScreenshotTest` tasks; they pass with nothing
 * to compare instead of failing on Gradle 9's `failOnNoDiscoveredTests`.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("com.android.compose.screenshot")

            extensions.getByType<CommonExtension>().apply {
                buildFeatures.compose = true
                experimentalProperties["android.experimental.enableScreenshotTest"] = true
                testOptions.unitTests.isIncludeAndroidResources = true
                // Robolectric 4.17 sets FileDescriptor internals through jdk.internal.access,
                // which java.base does not export to the test classpath on JDK 17+.
                testOptions.unitTests.all { test ->
                    test.jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
                }
            }

            dependencies {
                val composeBom = platform(libs.library("androidx-compose-bom"))
                add("implementation", composeBom)
                add("implementation", libs.library("androidx-compose-ui"))
                add("implementation", libs.library("androidx-compose-ui-tooling-preview"))
                add("implementation", libs.library("androidx-compose-material3"))
                add("implementation", libs.library("androidx-activity-compose"))
                add("implementation", libs.library("androidx-lifecycle-runtime-compose"))
                add("debugImplementation", libs.library("androidx-compose-ui-tooling"))
                add("debugImplementation", libs.library("androidx-compose-ui-test-manifest"))

                add("testImplementation", composeBom)
                add("testImplementation", libs.library("androidx-compose-ui-test-junit4"))
                add("testImplementation", libs.library("robolectric"))
                add("testImplementation", libs.library("junit4"))
                add("testRuntimeOnly", libs.library("junit-vintage-engine"))

                add("androidTestImplementation", composeBom)
                add("androidTestImplementation", libs.library("androidx-compose-ui-test-junit4"))

                add("screenshotTestImplementation", composeBom)
                add("screenshotTestImplementation", libs.library("compose-screenshot-validation-api"))
                add("screenshotTestImplementation", libs.library("androidx-compose-ui-tooling"))
            }
            // The screenshot plugin's validate/update tasks are Gradle Test tasks that always see
            // test sources, so Gradle 9 fails them in modules that have no @PreviewTest yet. Only
            // modules without a screenshotTest source set are exempt: one that has sources but
            // discovers no test is misconfigured and still fails.
            if (!layout.projectDirectory.dir("src/screenshotTest").asFile.exists()) {
                tasks.withType<Test>().configureEach {
                    if (name.endsWith("ScreenshotTest")) {
                        failOnNoDiscoveredTests.set(false)
                    }
                }
            }
            configureComposeStaticAnalysis()
        }
    }
}
