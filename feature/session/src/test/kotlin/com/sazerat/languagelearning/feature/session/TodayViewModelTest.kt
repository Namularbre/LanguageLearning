package com.sazerat.languagelearning.feature.session

import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.domain.model.Token
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
import com.sazerat.languagelearning.feature.session.today.NextText
import com.sazerat.languagelearning.feature.session.today.TodayUiState
import com.sazerat.languagelearning.feature.session.today.TodayViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class TodayViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val romanian = LanguageCode("ro-RO")
    private val today = 20L
    private val now = today * MILLIS_PER_DAY + 1_000

    private fun text(id: String, vararg lemmas: String) = ReadingText(
        id = TextId(id),
        languageCode = romanian,
        level = CefrLevel.A1,
        kind = TextKind.STORY,
        title = id,
        minutes = 3,
        sentences = listOf(Sentence(lemmas.map { Token(it, LemmaId(it)) }, LocalizedText.of(en = ""))),
    )

    private fun card(lemma: String, due: Long) =
        ReviewCard(LemmaId(lemma), romanian, CardContext(TextId("read"), 0, 0), memory = null, due = due, createdAt = 0)

    @Test
    fun `today shows the goal, what is due and the text to read next`() = runTest {
        val reviews = FakeReviewRepository()
        reviews.cards.value = List(5) { card("due$it", due = now - 1) } + card("later", due = now + MILLIS_PER_DAY)
        reviews.log.value = List(8) { ReviewLogEntry(LemmaId("x"), Rating.GOOD, now - 10) }
        val readings = FakeReadingLogRepository()
        readings.add(romanian, ReadingLogEntry(TextId("read"), 3, now - MILLIS_PER_DAY))
        val words = FakeWordStatusRepository()
        words.setStatus(romanian, listOf(LemmaId("a")), WordStatus.KNOWN)
        val viewModel = TodayViewModel(
            FakeUserPreferencesRepository(UserPreferences(learningLanguage = romanian, dailyGoal = DailyGoal.TWENTY)),
            reviews,
            readings,
            words,
            FakeTextRepository(listOf(text("read", "a"), text("next", "a", "b"))),
            FakeClock(now),
        )

        val state = viewModel.uiState.first { it is TodayUiState.Success } as TodayUiState.Success

        // 8 reviews today = 2 minutes; yesterday's reading keeps the streak going.
        assertEquals(2, state.minutesToday)
        assertEquals(20, state.goalMinutes)
        assertEquals(2, state.streak)
        assertEquals(5, state.dueCards)
        // 5 × 15 s, rounded up.
        assertEquals(2, state.reviewMinutes)
        assertEquals(NextText(TextId("next"), "next", CefrLevel.A1, TextKind.STORY, 3, 50), state.nextText)
    }

    @Test
    fun `once every text is read, the one read longest ago is offered again`() = runTest {
        val readings = FakeReadingLogRepository()
        readings.add(romanian, ReadingLogEntry(TextId("old"), 3, now - 2 * MILLIS_PER_DAY))
        readings.add(romanian, ReadingLogEntry(TextId("recent"), 3, now - MILLIS_PER_DAY))
        val viewModel = TodayViewModel(
            FakeUserPreferencesRepository(UserPreferences(learningLanguage = romanian)),
            FakeReviewRepository(),
            readings,
            FakeWordStatusRepository(),
            FakeTextRepository(listOf(text("old", "a"), text("recent", "b"))),
            FakeClock(now),
        )

        val state = viewModel.uiState.first { it is TodayUiState.Success } as TodayUiState.Success

        assertEquals(NextText(TextId("old"), "old", CefrLevel.A1, TextKind.STORY, 3, 0, isReread = true), state.nextText)
    }
}
