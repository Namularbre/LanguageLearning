package com.sazerat.languagelearning.feature.grammar

import androidx.lifecycle.SavedStateHandle
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import com.sazerat.languagelearning.feature.grammar.detail.LessonDetailUiState
import com.sazerat.languagelearning.feature.grammar.detail.LessonDetailViewModel
import com.sazerat.languagelearning.feature.grammar.list.LessonListUiState
import com.sazerat.languagelearning.feature.grammar.list.LessonListViewModel
import com.sazerat.languagelearning.feature.grammar.navigation.LessonDetail
import com.sazerat.languagelearning.feature.grammar.navigation.LessonList
import com.sazerat.languagelearning.speech.api.SpeakResult
import com.sazerat.languagelearning.speech.api.SpeechError
import com.sazerat.languagelearning.speech.api.SpeechRate
import com.sazerat.languagelearning.speech.fake.FakeTextToSpeech
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LessonViewModelsTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = object : LessonRepository {
        val lessons = MutableStateFlow(previewLessons)

        override fun observeLessons(languageCode: LanguageCode): Flow<List<Lesson>> =
            lessons.map { all -> all.filter { it.languageCode == languageCode } }

        override fun observeLesson(id: LessonId): Flow<Lesson?> = lessons.map { all -> all.find { it.id == id } }
    }

    private val textToSpeech = FakeTextToSpeech()

    private val lesson = previewLessons.first()
    private val example = lesson.sections.first().examples.first()

    @Test
    fun `list shows the lessons of the language in the route`() = runTest {
        val viewModel = LessonListViewModel(SavedStateHandle(mapOf(LessonList.LANGUAGE_CODE_KEY to "ro-RO")), repository)

        val state = viewModel.uiState.first { it is LessonListUiState.Success }

        assertEquals(LessonListUiState.Success(previewLessons), state)
    }

    @Test
    fun `list is empty for a language without lessons`() = runTest {
        val viewModel = LessonListViewModel(SavedStateHandle(mapOf(LessonList.LANGUAGE_CODE_KEY to "xx-TEST")), repository)

        val state = viewModel.uiState.first { it is LessonListUiState.Success }

        assertEquals(LessonListUiState.Success(emptyList()), state)
    }

    @Test
    fun `detail shows the lesson of the route`() = runTest {
        val state = detailViewModel(lesson.id.value).successState()

        assertEquals(LessonDetailUiState.Success(lesson), state)
    }

    @Test
    fun `detail reports an unknown id as not found`() = runTest {
        val viewModel = detailViewModel("missing")

        val state = viewModel.uiState.first { it !is LessonDetailUiState.Loading }

        assertEquals(LessonDetailUiState.NotFound, state)
    }

    @Test
    fun `tapping an example speaks it in the lesson language`() = runTest {
        val viewModel = detailViewModel(lesson.id.value)
        viewModel.successState()

        viewModel.onExampleClick(example)

        assertEquals(listOf(FakeTextToSpeech.Request(example.text, lesson.languageCode, SpeechRate.NORMAL)), textToSpeech.requests)
    }

    @Test
    fun `slow speed is used once selected`() = runTest {
        val viewModel = detailViewModel(lesson.id.value)
        viewModel.successState()

        viewModel.onSlowSpeedChange(true)
        viewModel.onExampleClick(example)

        assertEquals(SpeechRate.SLOW, textToSpeech.requests.single().rate)
        assertEquals(true, viewModel.successState().slowSpeed)
    }

    @Test
    fun `the example being spoken is marked as playing until speech ends`() = runTest {
        val gate = CompletableDeferred<Unit>()
        textToSpeech.gate = gate
        val viewModel = detailViewModel(lesson.id.value)
        viewModel.successState()

        viewModel.onExampleClick(example)
        assertEquals(example.text, viewModel.successState().playingText)

        gate.complete(Unit)
        assertNull(viewModel.successState().playingText)
    }

    @Test
    fun `a missing voice is reported, then cleared once shown`() = runTest {
        textToSpeech.result = SpeakResult.Failed(SpeechError.UNSUPPORTED_LANGUAGE)
        val viewModel = detailViewModel(lesson.id.value)
        viewModel.successState()

        viewModel.onExampleClick(example)
        assertEquals(SpeechError.UNSUPPORTED_LANGUAGE, viewModel.successState().speechError)

        viewModel.onSpeechErrorShown()
        assertNull(viewModel.successState().speechError)
    }

    private fun detailViewModel(lessonId: String) =
        LessonDetailViewModel(SavedStateHandle(mapOf(LessonDetail.LESSON_ID_KEY to lessonId)), repository, textToSpeech)

    // Subscribes to uiState (as the screen would) and returns the current Success state.
    private suspend fun LessonDetailViewModel.successState(): LessonDetailUiState.Success =
        uiState.first { it is LessonDetailUiState.Success } as LessonDetailUiState.Success
}
