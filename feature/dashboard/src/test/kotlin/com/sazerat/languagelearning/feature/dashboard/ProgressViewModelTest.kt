package com.sazerat.languagelearning.feature.dashboard

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import com.sazerat.languagelearning.core.testing.FakeClock
import com.sazerat.languagelearning.core.testing.FakeReadingLogRepository
import com.sazerat.languagelearning.core.testing.FakeReviewRepository
import com.sazerat.languagelearning.core.testing.FakeTextRepository
import com.sazerat.languagelearning.core.testing.FakeUserPreferencesRepository
import com.sazerat.languagelearning.core.testing.FakeWordStatusRepository
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val romanian = LanguageCode("ro-RO")
    private val today = 50L
    private val clock = FakeClock(now = today * MILLIS_PER_DAY + 1_000)

    @Test
    fun `progress sums up the words and the days of practice`() = runTest {
        val words = FakeWordStatusRepository()
        words.setStatus(romanian, listOf(LemmaId("ro:a"), LemmaId("ro:b")), WordStatus.KNOWN)
        words.setStatus(romanian, listOf(LemmaId("ro:c")), WordStatus.LEARNING)
        val reviews = FakeReviewRepository()
        reviews.log.value = listOf(ReviewLogEntry(LemmaId("ro:c"), Rating.GOOD, (today - 1) * MILLIS_PER_DAY))
        val readings = FakeReadingLogRepository()
        readings.add(romanian, ReadingLogEntry(TextId("t"), 3, today * MILLIS_PER_DAY))

        val viewModel = ProgressViewModel(
            FakeUserPreferencesRepository(UserPreferences(learningLanguage = romanian)),
            words,
            reviews,
            readings,
            FakeTextRepository(emptyList()),
            clock,
        )
        val state = viewModel.uiState.first { it is ProgressUiState.Success } as ProgressUiState.Success

        assertEquals(2, state.knownWords)
        assertEquals(1, state.learningWords)
        assertEquals(2, state.streak)
        assertEquals(ProgressUiState.CHART_DAYS, state.knownWordsByDay.size)
        // The fake marks words as updated at epoch 0: known on every day shown.
        assertEquals(2, state.knownWordsByDay.last())
        assertEquals(listOf(true, true), state.activeDays.takeLast(2))
        assertEquals(false, state.activeDays[state.activeDays.size - 3])
    }
}
