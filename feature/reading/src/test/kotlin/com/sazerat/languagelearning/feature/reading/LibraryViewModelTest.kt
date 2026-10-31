package com.sazerat.languagelearning.feature.reading

import androidx.lifecycle.SavedStateHandle
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.model.PronunciationExerciseType
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import com.sazerat.languagelearning.core.testing.FakeLessonRepository
import com.sazerat.languagelearning.core.testing.FakePronunciationRepository
import com.sazerat.languagelearning.core.testing.FakeReadingLogRepository
import com.sazerat.languagelearning.core.testing.FakeTextRepository
import com.sazerat.languagelearning.core.testing.FakeUserPreferencesRepository
import com.sazerat.languagelearning.core.testing.FakeWordStatusRepository
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import com.sazerat.languagelearning.feature.reading.library.LibrarySection
import com.sazerat.languagelearning.feature.reading.library.LibraryUiState
import com.sazerat.languagelearning.feature.reading.library.LibraryViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LibraryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val harder = previewText.copy(id = TextId("preview-harder"), level = CefrLevel.A2, title = "Mai greu")
    private val words = FakeWordStatusRepository()
    private val log = FakeReadingLogRepository()
    private val preferences = FakeUserPreferencesRepository(UserPreferences(learningLanguage = romanian))
    private val exercise = PronunciationExercise(
        id = ExerciseId("ro-ă"),
        languageCode = romanian,
        type = PronunciationExerciseType.MINIMAL_PAIR_LISTENING,
        title = LocalizedText.of(en = "ă"),
        focus = LocalizedText.of(en = "/ə/"),
        instructions = LocalizedText.of(en = "Listen."),
        items = emptyList(),
    )
    private val savedStateHandle = SavedStateHandle()

    private val viewModel = LibraryViewModel(
        savedStateHandle,
        preferences,
        FakeTextRepository(listOf(harder, previewText)),
        words,
        log,
        FakeLessonRepository(),
        FakePronunciationRepository(listOf(exercise)),
    )

    @Test
    fun `texts are listed easiest level first with their comprehension`() = runTest {
        words.setStatus(romanian, listOf(LemmaId("ro:eu"), LemmaId("ro:merge")), WordStatus.KNOWN)

        val state = viewModel.successState()

        assertEquals(listOf(previewText.id, harder.id), state.texts.map { it.id })
        // "Eu merg la piață. Merg mult.": eu, merg, merg known out of 6 occurrences.
        assertEquals(50, state.texts.first().comprehension)
    }

    @Test
    fun `a finished text is marked as read and not suggested`() = runTest {
        log.add(romanian, ReadingLogEntry(previewText.id, minutes = 2, finishedAt = 0))

        val state = viewModel.successState()

        assertTrue(state.texts.first { it.id == previewText.id }.isRead)
        assertEquals(harder.id, state.suggestedTextId)
    }

    @Test
    fun `the level filter keeps only that level and survives process death`() = runTest {
        viewModel.onLevelSelected(CefrLevel.A2)

        assertEquals(listOf(harder.id), viewModel.successState().texts.map { it.id })
        assertEquals("A2", savedStateHandle["level"])
    }

    @Test
    fun `pronunciation is offered only with the pronunciation help`() = runTest {
        assertEquals(listOf(LibrarySection.TEXTS, LibrarySection.LESSONS), viewModel.successState().sections)

        preferences.setPronunciationHelp(true)
        viewModel.onSectionSelected(LibrarySection.PRONUNCIATION)

        val state = viewModel.successState { LibrarySection.PRONUNCIATION in it.sections }
        assertEquals(LibrarySection.PRONUNCIATION, state.section)
        assertEquals(listOf(exercise), state.exercises)
    }

    @Test
    fun `turning the help off falls back to the texts`() = runTest {
        preferences.setPronunciationHelp(true)
        viewModel.onSectionSelected(LibrarySection.PRONUNCIATION)
        viewModel.successState { it.section == LibrarySection.PRONUNCIATION }

        preferences.setPronunciationHelp(false)

        val state = viewModel.successState { LibrarySection.PRONUNCIATION !in it.sections }
        assertEquals(LibrarySection.TEXTS, state.section)
    }

    // The preferences reach the state through a flatMapLatest: wait for the expected state rather
    // than reading the current one.
    private suspend fun LibraryViewModel.successState(
        until: (LibraryUiState.Success) -> Boolean = { true },
    ): LibraryUiState.Success = uiState.first { it is LibraryUiState.Success && until(it) } as LibraryUiState.Success
}
