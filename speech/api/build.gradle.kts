plugins {
    alias(libs.plugins.languagelearning.jvm.library)
}

kotlin {
    explicitApi()
}

dependencies {
    api(projects.core.domain)

    testImplementation(libs.kotlinx.coroutines.test)
}
