import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/** Enables Compose on an Android module that already applies the application or library plugin. */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            extensions.getByType<CommonExtension>().buildFeatures.compose = true
            dependencies {
                val bom = platform(libs.lib("androidx-compose-bom"))
                "implementation"(bom)
                "androidTestImplementation"(bom)
                "implementation"(libs.lib("androidx-compose-material3"))
                "implementation"(libs.lib("androidx-compose-ui"))
                "implementation"(libs.lib("androidx-compose-ui-tooling-preview"))
                "debugImplementation"(libs.lib("androidx-compose-ui-tooling"))
            }
        }
    }
}
