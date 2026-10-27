package com.arclabs.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Robolectric local tests for an Android module: Android resources on the unit-test classpath,
 * Robolectric + JUnit 4 (Robolectric's runner) executed by the JUnit Vintage engine next to
 * JUnit 5. Shared by `arc.android.robolectric`, `arc.android.compose` and `arc.android.room`.
 */
internal fun Project.configureRobolectricTests(commonExtension: CommonExtension) {
    commonExtension.testOptions.unitTests.isIncludeAndroidResources = true
    // Robolectric 4.17 sets FileDescriptor internals through jdk.internal.access,
    // which java.base does not export to the test classpath on JDK 17+.
    commonExtension.testOptions.unitTests.all { test ->
        test.jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
    }

    dependencies {
        add("testImplementation", libs.library("robolectric"))
        add("testImplementation", libs.library("junit4"))
        add("testRuntimeOnly", libs.library("junit-vintage-engine"))
    }
}
