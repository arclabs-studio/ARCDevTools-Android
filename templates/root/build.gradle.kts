// ARCDevTools-Android — root build template
// Version: 0.2.0
//
// Puts every Gradle plugin used by the arc.* convention plugins on the build classpath with a
// single version from gradle/libs.versions.toml. Modules apply the arc.* plugins only.
// AGP 9 has built-in Kotlin: never add org.jetbrains.kotlin.android or kapt.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room3) apply false
    alias(libs.plugins.android.junit5) apply false
    alias(libs.plugins.compose.screenshot) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}
