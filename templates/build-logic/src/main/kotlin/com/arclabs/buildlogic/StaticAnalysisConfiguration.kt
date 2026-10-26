package com.arclabs.buildlogic

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/** Bytecode level detekt analyses for; it otherwise defaults to the JVM running Gradle. */
private const val DETEKT_JVM_TARGET = "17"

/** detekt configuration copied by arc-setup, relative to the root project. */
private const val DETEKT_CONFIG = "config/detekt/detekt.yml"
private const val DETEKT_COMPOSE_CONFIG = "config/detekt/detekt-compose.yml"

/**
 * detekt (maxIssues: 0, from config/detekt/detekt.yml) and ktlint (rules from .editorconfig)
 * on every module. Both are wired into `check` by their plugins.
 */
internal fun Project.configureStaticAnalysis() {
    pluginManager.apply("io.gitlab.arturbosch.detekt")
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")

    val rootDirectory = isolated.rootProject.projectDirectory
    extensions.configure<DetektExtension> {
        config.setFrom(rootDirectory.file(DETEKT_CONFIG))
        buildUponDefaultConfig = true
        parallel = true
    }
    tasks.withType<Detekt>().configureEach { jvmTarget = DETEKT_JVM_TARGET }
    tasks.withType<DetektCreateBaselineTask>().configureEach { jvmTarget = DETEKT_JVM_TARGET }
    // detekt 1.23.x embeds its own Kotlin compiler; keep its classpath on the Kotlin version it
    // was built with instead of the project's Kotlin version (detekt's documented workaround).
    configurations.matching { it.name == "detekt" }.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") {
                useVersion(getSupportedKotlinVersion())
            }
        }
    }

    extensions.configure<KtlintExtension> {
        version.set(libs.version("ktlint"))
        outputToConsole.set(true)
        ignoreFailures.set(false)
    }
}

/** Adds the Compose rule set and config/detekt/detekt-compose.yml to detekt. */
internal fun Project.configureComposeStaticAnalysis() {
    val rootDirectory = isolated.rootProject.projectDirectory
    extensions.configure<DetektExtension> {
        config.from(rootDirectory.file(DETEKT_COMPOSE_CONFIG))
    }
    dependencies.add("detektPlugins", libs.library("compose-rules-detekt"))
}
