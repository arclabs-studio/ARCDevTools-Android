package com.arclabs.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

private val ARC_JAVA_VERSION = JavaVersion.VERSION_17
private val ARC_JVM_TARGET = JvmTarget.JVM_17

/**
 * SDK levels, Java/Kotlin bytecode level and zero-warning Kotlin for any Android module.
 *
 * AGP 9 compiles Kotlin itself (built-in Kotlin): the `org.jetbrains.kotlin.android` plugin
 * must never be applied; the `kotlin {}` extension is still registered by AGP.
 */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension) {
    commonExtension.apply {
        compileSdk = libs.versionInt("compileSdk")
        defaultConfig.minSdk = libs.versionInt("minSdk")
        compileOptions.sourceCompatibility = ARC_JAVA_VERSION
        compileOptions.targetCompatibility = ARC_JAVA_VERSION
    }
    extensions.configure<KotlinAndroidProjectExtension> {
        compilerOptions.applyArcDefaults()
    }
}

/** Java/Kotlin bytecode level and zero-warning Kotlin for a pure Kotlin/JVM module. */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = ARC_JAVA_VERSION
        targetCompatibility = ARC_JAVA_VERSION
    }
    extensions.configure<KotlinJvmProjectExtension> {
        compilerOptions.applyArcDefaults()
    }
}

private fun KotlinJvmCompilerOptions.applyArcDefaults() {
    jvmTarget.set(ARC_JVM_TARGET)
    allWarningsAsErrors.set(true)
}
