import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * A `:feature:*` module: Compose screens + Hilt ViewModels + type-safe navigation routes.
 * Features only see `:core:*` and `*:api` modules (spec §11.1), never an implementation.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("languagelearning.android.library")
            pluginManager.apply("languagelearning.android.compose")
            pluginManager.apply("languagelearning.hilt")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            dependencies {
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:ui"))
                "implementation"(libs.lib("androidx-navigation-compose"))
                "implementation"(libs.lib("androidx-hilt-lifecycle-viewmodel-compose"))
                "implementation"(libs.lib("androidx-lifecycle-runtime-compose"))
                "implementation"(libs.lib("androidx-lifecycle-viewmodel-compose"))
                "implementation"(libs.lib("kotlinx-serialization-json"))

                "testImplementation"(project(":core:testing"))

                "androidTestImplementation"(libs.lib("androidx-compose-ui-test-junit4"))
                "androidTestImplementation"(libs.lib("androidx-test-ext-junit"))
                "androidTestImplementation"(libs.lib("androidx-test-runner"))
                "androidTestImplementation"(libs.lib("androidx-test-espresso-core"))
                "debugImplementation"(libs.lib("androidx-compose-ui-test-manifest"))
            }
        }
    }
}
