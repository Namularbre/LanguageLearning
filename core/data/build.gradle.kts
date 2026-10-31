plugins {
    alias(libs.plugins.languagelearning.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

android {
    namespace = "com.sazerat.languagelearning.core.data"
}

// The schema of each database version is exported and committed: it documents the tables and is
// what future migrations are checked against.
room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    api(projects.core.domain)
    implementation(libs.kotlinx.coroutines.core)
    // api: :app builds the database and the DataStore, so it sees their types.
    api(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    api(libs.androidx.datastore.preferences)

    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
