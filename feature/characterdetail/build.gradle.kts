plugins {
    alias(libs.plugins.rickandmorty.android.feature)
}

android {
    namespace = "ninja.droiddojo.rickandmorty.feature.characterdetail"
}

dependencies {
    // only what THIS module specifically needs
    implementation(projects.core.data.api)
}
