import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/** Hilt with KSP (never kapt, spec §9.9). The Hilt Gradle plugin is only needed in the application module. */
class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.withPlugin("com.android.application") {
                pluginManager.apply("com.google.dagger.hilt.android")
            }
            dependencies {
                "implementation"(libs.lib("hilt-android"))
                "ksp"(libs.lib("hilt-compiler"))
            }
        }
    }
}
