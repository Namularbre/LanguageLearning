package com.sazerat.languagelearning.feature.grammar.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.feature.grammar.detail.LessonDetailScreen
import com.sazerat.languagelearning.feature.grammar.list.LessonListScreen
import kotlinx.serialization.Serializable

@Serializable
data class LessonList(val languageCode: String) {
    companion object {
        /** Navigation saves each route property under its own name; must match the property above. */
        const val LANGUAGE_CODE_KEY = "languageCode"
    }
}

@Serializable
data class LessonDetail(val lessonId: String) {
    companion object {
        const val LESSON_ID_KEY = "lessonId"
    }
}

/** The feature works for any language: the caller decides which one (spec §11.2). */
fun NavController.navigateToLessonList(languageCode: LanguageCode) {
    navigate(LessonList(languageCode = languageCode.value))
}

fun NavGraphBuilder.grammarScreens(navController: NavController) {
    composable<LessonList> {
        LessonListScreen(
            onLessonClick = { id -> navController.navigate(LessonDetail(lessonId = id.value)) },
            onBack = { navController.navigateUp() },
        )
    }
    composable<LessonDetail> {
        LessonDetailScreen(onBack = { navController.navigateUp() })
    }
}
