package com.sazerat.languagelearning.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.feature.grammar.navigation.LessonDetail
import com.sazerat.languagelearning.feature.grammar.navigation.grammarScreens
import com.sazerat.languagelearning.feature.grammar.navigation.navigateToLessonList
import com.sazerat.languagelearning.feature.pronunciation.navigation.PronunciationDetail
import com.sazerat.languagelearning.feature.pronunciation.navigation.navigateToPronunciationList
import com.sazerat.languagelearning.feature.reading.navigation.navigateToLibrary
import com.sazerat.languagelearning.feature.reading.navigation.navigateToReader
import com.sazerat.languagelearning.feature.reading.navigation.readingScreens
import com.sazerat.languagelearning.feature.pronunciation.navigation.pronunciationScreens
import com.sazerat.languagelearning.feature.session.navigation.Home
import com.sazerat.languagelearning.feature.session.navigation.homeScreen
import com.sazerat.languagelearning.feature.settings.navigation.settingsScreen
import com.sazerat.languagelearning.feature.srs.navigation.navigateToReview
import com.sazerat.languagelearning.feature.srs.navigation.reviewScreen

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
            onOpenLibrary = { navController.navigateToLibrary() },
            onOpenReview = { navController.navigateToReview() },
        )
        pronunciationScreens(navController)
        grammarScreens(navController)
        readingScreens(
            navController = navController,
            onLessonClick = { id -> navController.navigate(LessonDetail(lessonId = id.value)) },
            onExerciseClick = { id -> navController.navigate(PronunciationDetail(exerciseId = id.value)) },
        )
        reviewScreen(navController, onReadText = { id -> navController.navigateToReader(id) })
        settingsScreen(navController, languages = studiedLanguages)
    }
}

// Languages offered on Home. M2 will derive them from the downloaded content packs.
private val studiedLanguages = listOf(LanguageCode("en-GB"), LanguageCode("ro-RO"), LanguageCode("fr-FR"))
