plugins {
    alias(libs.plugins.arc.android.feature)
    alias(libs.plugins.arc.android.room)
}

android {
    namespace = "com.arclabs.fixture.feature.sample"

    // Every Android module declares the app's flavors so the DevDebug variant (and its
    // test/lint/coverage/screenshot tasks) exists in each of them.
    flavorDimensions += "environment"
    productFlavors {
        create("dev") { dimension = "environment" }
        create("pro") { dimension = "environment" }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core:ui"))
}
