plugins {
    alias(libs.plugins.languagelearning.android.library)
}

android {
    namespace = "com.sazerat.languagelearning.speech.android"
}

dependencies {
    api(projects.speech.api)
    implementation(libs.kotlinx.coroutines.core)
}
