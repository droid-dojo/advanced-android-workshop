plugins {
    alias(libs.plugins.rickandmorty.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "ninja.droiddojo.rickandmorty.core.analytics.impl"
}

dependencies {
    // The bound interfaces appear in this module's public @Binds signatures -> api
    api(projects.core.analytics.api)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
