package com.sazerat.languagelearning.feature.pronunciation

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.ui.theme.LanguageLearningTheme
import com.sazerat.languagelearning.feature.pronunciation.list.PronunciationListContent
import com.sazerat.languagelearning.feature.pronunciation.list.PronunciationListUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PronunciationListContentTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun clickingAnExerciseReportsItsId() {
        var clicked: ExerciseId? = null
        composeRule.setContent {
            LanguageLearningTheme(dynamicColor = false) {
                PronunciationListContent(
                    languageCode = LanguageCode("en-GB"),
                    uiState = PronunciationListUiState.Success(previewExercises),
                    onExerciseClick = { clicked = it },
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithText("think / sink").performClick()

        assertEquals(ExerciseId("preview-think-sink"), clicked)
    }
}
