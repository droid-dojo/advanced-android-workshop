pluginManagement {
    // Our convention plugins (rickandmorty.android.*) live in this included build
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "rick-and-morty"
include(":app")
include(":core:model")
include(":core:ui")
include(":core:data:api")
include(":core:data:impl")
include(":core:analytics:api")
include(":core:analytics:impl")
include(":core:settings:api")
include(":core:settings:impl")
include(":feature:characterlist")
include(":feature:characterdetail")
