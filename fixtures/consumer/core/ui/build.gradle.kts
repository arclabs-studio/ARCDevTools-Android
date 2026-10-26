plugins {
    alias(libs.plugins.arc.android.library)
    alias(libs.plugins.arc.android.compose)
}

android {
    namespace = "com.arclabs.fixture.core.ui"

    // Every Android module declares the app's flavors so the DevDebug variant (and its
    // test/lint/coverage/screenshot tasks) exists in each of them.
    flavorDimensions += "environment"
    productFlavors {
        create("dev") { dimension = "environment" }
        create("pro") { dimension = "environment" }
    }
}
