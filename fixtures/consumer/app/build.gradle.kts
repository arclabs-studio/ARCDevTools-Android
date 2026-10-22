plugins {
    alias(libs.plugins.arc.android.application)
    alias(libs.plugins.arc.android.compose)
    alias(libs.plugins.arc.android.hilt)
}

android {
    namespace = "com.arclabs.fixture"

    defaultConfig {
        applicationId = "com.arclabs.fixture"
        versionCode = 1
        versionName = "1.0"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
        }
        create("pro") { dimension = "environment" }
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":feature:sample"))
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
}
