plugins {
    alias(libs.plugins.rickandmorty.android.feature)
}

android {
    namespace = "ninja.droiddojo.rickandmorty.feature.characterlist"
}

dependencies {
    // only what THIS module specifically needs
    implementation(projects.core.data)
}
