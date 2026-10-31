pluginManagement {
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

rootProject.name = "LanguageLearning"

// Module layout: see SPEC.md §11.1.
include(":app")

include(":core:domain")
include(":core:data")
include(":core:network")
include(":core:audio")
include(":core:ui")
include(":core:testing")

include(":ai:api")
include(":ai:gemini")
include(":ai:mlkit")
include(":ai:fake")

include(":speech:api")
include(":speech:azure")
include(":speech:android")
include(":speech:fake")

include(":lang:api")
include(":lang:en-gb")
include(":lang:ro-ro")
include(":lang:test")

include(":feature:pronunciation")
include(":feature:grammar")
include(":feature:reading")
include(":feature:oral")
include(":feature:session")
include(":feature:srs")
include(":feature:dashboard")
include(":feature:settings")
