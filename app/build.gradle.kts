plugins {
    alias(libs.plugins.languagelearning.android.application)
    alias(libs.plugins.languagelearning.android.compose)
    alias(libs.plugins.languagelearning.hilt)
}

android {
    namespace = "com.sazerat.languagelearning"

    defaultConfig {
        applicationId = "com.sazerat.languagelearning"
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
}

dependencies {
    // Only :app sees implementations and binds them with Hilt (spec §11.1).
    implementation(projects.core.domain)
    implementation(projects.core.data)
    implementation(projects.core.ui)
    implementation(projects.speech.android)

    implementation(projects.feature.pronunciation)
    implementation(projects.feature.grammar)
    implementation(projects.feature.session)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
}
