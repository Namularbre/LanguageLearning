plugins {
    alias(libs.plugins.languagelearning.android.library)
    alias(libs.plugins.languagelearning.android.compose)
}

android {
    namespace = "com.sazerat.languagelearning.core.ui"
}

dependencies {
    api(libs.androidx.compose.material.icons.core)
    api(projects.speech.api)
}
