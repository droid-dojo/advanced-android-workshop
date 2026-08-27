plugins {
    alias(libs.plugins.rickandmorty.android.library)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "ninja.droiddojo.rickandmorty.core.ui"
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.coil.compose)
}
