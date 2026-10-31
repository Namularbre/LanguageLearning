plugins {
    alias(libs.plugins.languagelearning.android.feature)
}

android {
    namespace = "com.sazerat.languagelearning.feature.srs"
}

dependencies {
    implementation(projects.speech.api)

    testImplementation(projects.speech.fake)
}
