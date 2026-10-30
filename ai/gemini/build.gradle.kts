plugins {
    alias(libs.plugins.languagelearning.android.library)
}

android {
    namespace = "com.sazerat.languagelearning.ai.gemini"
}

dependencies {
    api(projects.ai.api)
}
