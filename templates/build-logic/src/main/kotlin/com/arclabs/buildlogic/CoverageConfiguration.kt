package com.arclabs.buildlogic

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.gradle.testing.jacoco.tasks.JacocoReportBase
import java.math.BigDecimal

/** Minimum line coverage enforced by `check` (ARC standard: 80% for apps). */
private val MINIMUM_LINE_COVERAGE = BigDecimal("0.80")

/** Build type whose unit tests run (AGP 9 only creates unit tests for the tested build type). */
private const val COVERED_BUILD_TYPE = "debug"

/**
 * Code that unit tests cannot or should not measure: generated code (R, BuildConfig, Hilt/Dagger,
 * Room `_Impl`, Compose singletons) and `@Preview` functions, which ARC keeps in `*Previews.kt`
 * files — they are exercised by the screenshot tests, not by unit tests.
 */
private val COVERAGE_EXCLUSIONS = listOf(
    "**/*PreviewsKt.class",
    "**/*PreviewsKt$*.class",
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/ComposableSingletons$*.*",
    "**/*_Factory.class",
    "**/*_Factory$*.class",
    "**/*_MembersInjector.class",
    "**/*_HiltModules*.*",
    "**/*_HiltComponents*.*",
    "**/Dagger*_HiltComponents*.*",
    "**/*_ComponentTreeDeps*.*",
    "**/*_GeneratedInjector*.*",
    "**/Hilt_*.*",
    "**/hilt_aggregated_deps/**",
    "**/dagger/hilt/internal/**",
    "**/*_Impl.class",
    "**/*_Impl$*.class",
)

/**
 * JaCoCo coverage on top of AGP's native unit-test coverage (`enableUnitTestCoverage`).
 *
 * For the debug variant it registers `jacocoDebugUnitTestReport` (XML + HTML) and
 * `jacocoDebugUnitTestCoverageVerification` (>= 80% lines), and makes `check` depend on the
 * verification. Classes are taken from the variant's public `ScopedArtifact.CLASSES`.
 */
internal fun Project.configureAndroidCoverage(androidComponents: AndroidComponentsExtension<*, *, *>) {
    pluginManager.apply("jacoco")
    // Robolectric loads classes through its own class loader: record them too.
    tasks.withType<Test>().configureEach {
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
    androidComponents.onVariants(androidComponents.selector().withBuildType(COVERED_BUILD_TYPE)) { variant ->
        val variantName = variant.name.replaceFirstChar(Char::titlecase)
        val unitTestTask = "test${variantName}UnitTest"
        val executionData = layout.buildDirectory
            .dir("outputs/unit_test_code_coverage/${variant.name}UnitTest")
            .map { dir -> dir.asFileTree.matching { include("**/*.exec") } }
        val sourceDirectories = listOfNotNull(variant.sources.java?.all, variant.sources.kotlin?.all)
        val objectFactory = objects
        val classJars = objectFactory.listProperty(RegularFile::class.java)
        val classDirectories = objectFactory.listProperty(Directory::class.java)

        fun JacocoReportBase.configureInputs() {
            dependsOn(unitTestTask)
            this.executionData.setFrom(executionData)
            this.sourceDirectories.setFrom(sourceDirectories)
            this.classDirectories.setFrom(
                classJars,
                classDirectories.map { dirs ->
                    dirs.map { dir -> objectFactory.fileTree().setDir(dir).exclude(COVERAGE_EXCLUSIONS) }
                },
            )
        }

        val report = tasks.register<JacocoReport>("jacoco${variantName}UnitTestReport") {
            group = "verification"
            description = "JaCoCo coverage report for the $variantName unit tests."
            configureInputs()
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }
        val verification = tasks.register<JacocoCoverageVerification>(
            "jacoco${variantName}UnitTestCoverageVerification",
        ) {
            group = "verification"
            description = "Fails when $variantName unit-test line coverage is below 80%."
            configureInputs()
            violationRules {
                rule {
                    limit {
                        counter = "LINE"
                        minimum = MINIMUM_LINE_COVERAGE
                    }
                }
            }
        }
        listOf(report, verification).forEach { task ->
            variant.artifacts
                .forScope(ScopedArtifacts.Scope.PROJECT)
                .use(task)
                .toGet(ScopedArtifact.CLASSES, { _ -> classJars }, { _ -> classDirectories })
        }
        tasks.named("check") { dependsOn(verification) }
    }
}

/** JaCoCo coverage for a pure Kotlin/JVM module: report + >= 80% line verification on `check`. */
internal fun Project.configureJvmCoverage() {
    pluginManager.apply("jacoco")
    val report = tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn("test")
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
    val verification = tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        dependsOn("test")
        violationRules {
            rule {
                limit {
                    counter = "LINE"
                    minimum = MINIMUM_LINE_COVERAGE
                }
            }
        }
    }
    tasks.named("check") { dependsOn(report, verification) }
}
