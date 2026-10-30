plugins {
    alias(libs.plugins.languagelearning.jvm.library)
}

dependencies {
    api(projects.speech.api)
    implementation(libs.kotlinx.coroutines.core)
}
