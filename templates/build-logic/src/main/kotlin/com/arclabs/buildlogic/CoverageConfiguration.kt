package com.arclabs.buildlogic

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import org.gradle.api.GradleException
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

/** Gradle property with the minimum line coverage enforced by `check`, as a ratio in (0, 1]. */
private const val MINIMUM_LINE_COVERAGE_PROPERTY = "arc.coverage.minimum"

/**
 * Minimum line coverage when [MINIMUM_LINE_COVERAGE_PROPERTY] is not set. ARC rule: 0.80 for every
 * module inside an app repo; extracted ARC*-Android packages set the property to 1.00.
 */
private val DEFAULT_MINIMUM_LINE_COVERAGE = BigDecimal("0.80")

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
 * Extra-properties key an Android module sets to exclude more classes from its coverage: a list of Ant
 * patterns like [COVERAGE_EXCLUSIONS], set in its build file with a comment saying why. Meant for
 * SDK glue no local test can exercise (e.g. Firebase Remote Config or Analytics adapters):
 *
 * ```kotlin
 * // Remote Config has no local emulator: the adapter is covered by the emulator-free fakes.
 * extra["arc.coverage.excludes"] = listOf("**/RemoteConfigFeatureFlagRepository*")
 * ```
 */
private const val EXTRA_COVERAGE_EXCLUSIONS_PROPERTY = "arc.coverage.excludes"

/** The module's [EXTRA_COVERAGE_EXCLUSIONS_PROPERTY] patterns; empty when it sets none. */
private fun Project.extraCoverageExclusions(): List<String> =
    (extensions.extraProperties.properties[EXTRA_COVERAGE_EXCLUSIONS_PROPERTY] as? List<*>)
        ?.filterIsInstance<String>()
        .orEmpty()

/**
 * The minimum line coverage for this build: Gradle property `arc.coverage.minimum`
 * (`gradle.properties` or `-Parc.coverage.minimum=…`), default 0.80. Fails configuration when the
 * value is not a number in (0, 1].
 */
private fun Project.minimumLineCoverage(): BigDecimal {
    val property = providers.gradleProperty(MINIMUM_LINE_COVERAGE_PROPERTY).orNull
        ?: return DEFAULT_MINIMUM_LINE_COVERAGE
    val minimum = property.trim().toBigDecimalOrNull()
    if (minimum == null || minimum <= BigDecimal.ZERO || minimum > BigDecimal.ONE) {
        throw GradleException(
            "Gradle property '$MINIMUM_LINE_COVERAGE_PROPERTY' must be a ratio in (0, 1], " +
                "e.g. 0.80 (apps) or 1.00 (ARC*-Android packages); found '$property'.",
        )
    }
    return minimum
}

/** [BigDecimal] ratio as a percentage for task descriptions, e.g. 0.80 -> "80". */
private fun BigDecimal.asPercent(): String = movePointRight(2).stripTrailingZeros().toPlainString()

/**
 * JaCoCo coverage on top of AGP's native unit-test coverage (`enableUnitTestCoverage`).
 *
 * For every variant of the debug build type (`debug`, or e.g. `devDebug` with flavors) it
 * registers `jacoco<Variant>UnitTestReport` (XML + HTML) and
 * `jacoco<Variant>UnitTestCoverageVerification` (line coverage >= `arc.coverage.minimum`, default
 * 80%), and makes `check` depend on the verification. Classes are taken from the variant's public
 * `ScopedArtifact.CLASSES`.
 */
internal fun Project.configureAndroidCoverage(androidComponents: AndroidComponentsExtension<*, *, *>) {
    val minimumLineCoverage = minimumLineCoverage()
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
        // Read here, after the module's build script has run.
        val exclusions = COVERAGE_EXCLUSIONS + extraCoverageExclusions()
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
                    dirs.map { dir -> objectFactory.fileTree().setDir(dir).exclude(exclusions) }
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
            description = "Fails when $variantName unit-test line coverage is below " +
                "${minimumLineCoverage.asPercent()}% (arc.coverage.minimum)."
            configureInputs()
            violationRules {
                rule {
                    limit {
                        counter = "LINE"
                        minimum = minimumLineCoverage
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

/**
 * JaCoCo coverage for a pure Kotlin/JVM module: report + line verification on `check`
 * (>= `arc.coverage.minimum`, default 80%).
 */
internal fun Project.configureJvmCoverage() {
    val minimumLineCoverage = minimumLineCoverage()
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
                    minimum = minimumLineCoverage
                }
            }
        }
    }
    tasks.named("check") { dependsOn(report, verification) }
}
