package com.sazerat.languagelearning.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.feature.grammar.navigation.grammarScreens
import com.sazerat.languagelearning.feature.grammar.navigation.navigateToLessonList
import com.sazerat.languagelearning.feature.pronunciation.navigation.navigateToPronunciationList
import com.sazerat.languagelearning.feature.pronunciation.navigation.pronunciationScreens
import com.sazerat.languagelearning.feature.session.navigation.Home
import com.sazerat.languagelearning.feature.session.navigation.homeScreen

/**
 * The whole navigation graph. Each feature contributes its screens; only the app connects them.
 * rememberNavController() saves the back stack, so it is restored after process death.
 */
@Composable
fun LanguageLearningNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        homeScreen(
            languages = studiedLanguages,
            onOpenPronunciation = { language -> navController.navigateToPronunciationList(language) },
            onOpenLessons = { language -> navController.navigateToLessonList(language) },
        )
        pronunciationScreens(navController)
        grammarScreens(navController)
    }
}

// Languages offered on Home. M2 will derive them from the downloaded content packs.
private val studiedLanguages = listOf(LanguageCode("en-GB"), LanguageCode("ro-RO"), LanguageCode("fr-FR"))
