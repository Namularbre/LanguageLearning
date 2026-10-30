plugins {
    alias(libs.plugins.languagelearning.android.library)
}

android {
    namespace = "com.sazerat.languagelearning.core.data"
}

dependencies {
    api(projects.core.domain)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlinx.coroutines.test)
}
