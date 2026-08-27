plugins {
    alias(libs.plugins.rickandmorty.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "ninja.droiddojo.rickandmorty.core.settings.impl"
}

dependencies {
    // The implemented contract appears in this module's public signatures -> api
    api(projects.core.settings.api)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
