plugins {
    alias(libs.plugins.languagelearning.android.library)
}

android {
    namespace = "com.sazerat.languagelearning.ai.mlkit"
}

dependencies {
    api(projects.ai.api)
}
