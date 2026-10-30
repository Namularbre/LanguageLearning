package com.sazerat.languagelearning.feature.pronunciation

import androidx.lifecycle.SavedStateHandle
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import com.sazerat.languagelearning.feature.pronunciation.detail.PronunciationDetailUiState
import com.sazerat.languagelearning.feature.pronunciation.detail.PronunciationDetailViewModel
import com.sazerat.languagelearning.feature.pronunciation.list.PronunciationListUiState
import com.sazerat.languagelearning.feature.pronunciation.list.PronunciationListViewModel
import com.sazerat.languagelearning.feature.pronunciation.navigation.PronunciationDetail
import com.sazerat.languagelearning.feature.pronunciation.navigation.PronunciationList
import com.sazerat.languagelearning.speech.api.SpeechRate
import com.sazerat.languagelearning.speech.fake.FakeTextToSpeech
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class PronunciationViewModelsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakePronunciationRepository(previewExercises)
    private val textToSpeech = FakeTextToSpeech()

    @Test
    fun `list shows the exercises of the language in the route`() = runTest {
        val viewModel = listViewModel("en-GB")

        val state = viewModel.uiState.first { it is PronunciationListUiState.Success }

        assertEquals(PronunciationListUiState.Success(previewExercises), state)
    }

    @Test
    fun `list is empty for a language without exercises`() = runTest {
        val viewModel = listViewModel("xx-TEST")

        val state = viewModel.uiState.first { it is PronunciationListUiState.Success }

        assertEquals(PronunciationListUiState.Success(emptyList()), state)
    }

    @Test
    fun `list follows repository updates`() = runTest {
        val viewModel = listViewModel("en-GB")
        viewModel.uiState.first { it is PronunciationListUiState.Success }

        repository.exercises.value = previewExercises.take(1)

        assertEquals(PronunciationListUiState.Success(previewExercises.take(1)), viewModel.uiState.first())
    }

    @Test
    fun `detail reads the exercise id from the route arguments`() = runTest {
        val exercise = previewExercises.last()
        val viewModel = detailViewModel(exercise.id.value)

        val state = viewModel.uiState.first { it !is PronunciationDetailUiState.Loading }

        assertEquals(PronunciationDetailUiState.Success(exercise), state)
    }

    @Test
    fun `detail reports an unknown id as not found`() = runTest {
        val viewModel = detailViewModel("missing")

        val state = viewModel.uiState.first { it !is PronunciationDetailUiState.Loading }

        assertEquals(PronunciationDetailUiState.NotFound, state)
    }

    @Test
    fun `tapping an item speaks it in the exercise language, slowly if asked`() = runTest {
        val exercise = previewExercises.first()
        val viewModel = detailViewModel(exercise.id.value)
        viewModel.uiState.first { it is PronunciationDetailUiState.Success }

        viewModel.onItemClick(exercise.items[0])
        viewModel.onSlowSpeedChange(true)
        viewModel.onItemClick(exercise.items[1])

        assertEquals(
            listOf(
                FakeTextToSpeech.Request(exercise.items[0].text, exercise.languageCode, SpeechRate.NORMAL),
                FakeTextToSpeech.Request(exercise.items[1].text, exercise.languageCode, SpeechRate.SLOW),
            ),
            textToSpeech.requests,
        )
    }

    private fun listViewModel(languageCode: String) =
        PronunciationListViewModel(SavedStateHandle(mapOf(PronunciationList.LANGUAGE_CODE_KEY to languageCode)), repository)

    private fun detailViewModel(exerciseId: String) =
        PronunciationDetailViewModel(detailHandle(exerciseId), repository, textToSpeech)

    // Same shape as what Navigation puts in the handle for PronunciationDetail(exerciseId).
    private fun detailHandle(exerciseId: String) =
        SavedStateHandle(mapOf(PronunciationDetail.EXERCISE_ID_KEY to exerciseId))
}
