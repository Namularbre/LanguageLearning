package com.sazerat.languagelearning.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.feature.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable
data object Settings

fun NavController.navigateToSettings() {
    navigate(Settings)
}

/** [languages] are the languages that have content: the app knows them, not this feature. */
fun NavGraphBuilder.settingsScreen(navController: NavController, languages: List<LanguageCode>) {
    composable<Settings> {
        SettingsScreen(languages = languages, onBack = { navController.navigateUp() })
    }
}
