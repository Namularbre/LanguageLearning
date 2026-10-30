plugins {
    alias(libs.plugins.languagelearning.android.feature)
}

android {
    namespace = "com.sazerat.languagelearning.feature.grammar"
}

dependencies {
    implementation(projects.speech.api)

    testImplementation(projects.speech.fake)
}
