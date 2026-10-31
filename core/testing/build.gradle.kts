plugins {
    alias(libs.plugins.languagelearning.jvm.library)
}

// Test helpers shared by the unit tests of every module.
dependencies {
    api(projects.core.domain)
    api(libs.kotlinx.coroutines.test)
    api(libs.kotlin.test)
}
