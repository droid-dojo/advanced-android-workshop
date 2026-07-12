plugins {
    alias(libs.plugins.rickandmorty.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ninja.droiddojo.rickandmorty.core.settings.api"
}

dependencies {
    // TerminalConfig (@Serializable) and Flow appear in the contract -> api
    api(libs.kotlinx.serialization.json)
    api(libs.kotlinx.coroutines.core)
}
