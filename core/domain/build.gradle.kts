plugins {
    alias(libs.plugins.languagelearning.jvm.library)
}

kotlin {
    explicitApi()
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
