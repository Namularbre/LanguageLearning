package com.sazerat.languagelearning.feature.reading.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.feature.reading.library.LibraryScreen
import com.sazerat.languagelearning.feature.reading.reader.ReaderScreen
import kotlinx.serialization.Serializable

/** The Library tab: texts of the language being learnt (from the preferences, not from the route). */
@Serializable
data object Library

@Serializable
data class Reader(val textId: String) {
    companion object {
        /** Navigation saves each route property under its own name; must match the property above. */
        const val TEXT_ID_KEY = "textId"
    }
}

fun NavController.navigateToReader(textId: TextId) {
    navigate(Reader(textId = textId.value))
}

/**
 * Lessons and pronunciation exercises belong to other features: the app says where they open
 * (spec §11.2), so this module does not depend on them.
 */
fun NavGraphBuilder.readingScreens(
    navController: NavController,
    onLessonClick: (LessonId) -> Unit,
    onExerciseClick: (ExerciseId) -> Unit,
    languageSwitcher: @Composable () -> Unit,
) {
    composable<Library> {
        LibraryScreen(
            onTextClick = { id -> navController.navigateToReader(id) },
            onLessonClick = onLessonClick,
            onExerciseClick = onExerciseClick,
            // A tab: nothing to go back to.
            onBack = null,
            languageSwitcher = languageSwitcher,
        )
    }
    composable<Reader> {
        ReaderScreen(onBack = { navController.navigateUp() })
    }
}
