package com.sazerat.languagelearning.feature.session.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.feature.session.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object Home

/** [languages] are the languages offered on Home, in display order; the first one is selected by default. */
fun NavGraphBuilder.homeScreen(
    languages: List<LanguageCode>,
    onOpenPronunciation: (LanguageCode) -> Unit,
    onOpenLessons: (LanguageCode) -> Unit,
    onOpenLibrary: () -> Unit,
) {
    composable<Home> {
        HomeScreen(
            languages = languages,
            onOpenPronunciation = onOpenPronunciation,
            onOpenLessons = onOpenLessons,
            onOpenLibrary = onOpenLibrary,
        )
    }
}
