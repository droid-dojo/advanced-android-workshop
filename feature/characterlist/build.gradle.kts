plugins {
    alias(libs.plugins.rickandmorty.android.feature)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.screenshot)
}

android {
    namespace = "ninja.droiddojo.rickandmorty.feature.characterlist"
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
    // only what THIS module specifically needs
    implementation(projects.core.data.api)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)

    screenshotTestImplementation(libs.screenshot.validation.api)
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)
}
