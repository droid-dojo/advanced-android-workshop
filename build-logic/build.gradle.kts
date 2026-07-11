plugins {
    `kotlin-dsl`
}

dependencies {
    // compileOnly: the plugin classes only need AGP's types at compile time -
    // at runtime AGP is already on the build classpath (root build.gradle.kts)
    compileOnly(libs.android.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "rickandmorty.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "rickandmorty.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
    }
}
