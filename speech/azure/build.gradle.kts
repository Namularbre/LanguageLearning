plugins {
    alias(libs.plugins.languagelearning.android.library)
}

android {
    namespace = "com.sazerat.languagelearning.speech.azure"
}

dependencies {
    api(projects.speech.api)
}
