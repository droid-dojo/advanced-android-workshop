plugins {
    alias(libs.plugins.rickandmorty.android.library)
}

android {
    namespace = "ninja.droiddojo.rickandmorty.core.data.api"
}

dependencies {
    // Character & Flow appear in the contract's signatures -> api
    api(projects.core.model)
    api(libs.kotlinx.coroutines.core)
}
