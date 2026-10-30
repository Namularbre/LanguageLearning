package com.sazerat.languagelearning.feature.pronunciation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.feature.pronunciation.detail.PronunciationDetailScreen
import com.sazerat.languagelearning.feature.pronunciation.list.PronunciationListScreen
import kotlinx.serialization.Serializable

// Type-safe routes (spec §9.5). Arguments are primitives so they fit in the saved back stack.

@Serializable
data class PronunciationList(val languageCode: String) {
    companion object {
        /** Navigation saves each route property under its own name; must match the property above. */
        const val LANGUAGE_CODE_KEY = "languageCode"
    }
}

@Serializable
data class PronunciationDetail(val exerciseId: String) {
    companion object {
        /** Navigation saves each route property under its own name; must match the property above. */
        const val EXERCISE_ID_KEY = "exerciseId"
    }
}

/** The feature works for any language: the caller decides which one (spec §11.2). */
fun NavController.navigateToPronunciationList(languageCode: LanguageCode) {
    navigate(PronunciationList(languageCode = languageCode.value))
}

fun NavGraphBuilder.pronunciationScreens(navController: NavController) {
    composable<PronunciationList> {
        PronunciationListScreen(
            onExerciseClick = { id -> navController.navigate(PronunciationDetail(exerciseId = id.value)) },
            onBack = { navController.navigateUp() },
        )
    }
    composable<PronunciationDetail> {
        PronunciationDetailScreen(onBack = { navController.navigateUp() })
    }
}
