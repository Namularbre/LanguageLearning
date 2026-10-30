plugins {
    alias(libs.plugins.languagelearning.android.feature)
}

android {
    namespace = "com.sazerat.languagelearning.feature.pronunciation"
}

dependencies {
    implementation(projects.speech.api)

    testImplementation(projects.speech.fake)
}
