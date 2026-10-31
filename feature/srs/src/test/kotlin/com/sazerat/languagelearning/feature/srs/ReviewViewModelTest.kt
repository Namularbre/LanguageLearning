package com.sazerat.languagelearning.feature.srs

import androidx.lifecycle.SavedStateHandle
import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LearnerWord
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.PartOfSpeech
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
import com.sazerat.languagelearning.core.domain.repository.LexiconRepository
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import com.sazerat.languagelearning.core.testing.FakeReviewRepository
import com.sazerat.languagelearning.core.testing.FakeUserPreferencesRepository
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import com.sazerat.languagelearning.feature.srs.review.ReviewUiState
import com.sazerat.languagelearning.feature.srs.review.ReviewViewModel
import com.sazerat.languagelearning.speech.fake.FakeTextToSpeech
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReviewViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val romanian = LanguageCode("ro-RO")
    private val now = 100 * MILLIS_PER_DAY
    private val clock = object : Clock {
        override fun now() = now

        override fun dayOf(millis: Long) = millis / MILLIS_PER_DAY
    }

    private val text = ReadingText(
        id = TextId("t"),
        languageCode = romanian,
        level = CefrLevel.A1,
        kind = TextKind.STORY,
        title = "La piață",
        minutes = 2,
        sentences = listOf(
            Sentence(
                tokens = listOf(Token("Merg", LemmaId("ro:merge")), Token(" "), Token("la", LemmaId("ro:la")), Token(" "), Token("piață", LemmaId("ro:piață")), Token(".")),
                translation = LocalizedText.of(en = "I go to the market."),
            ),
        ),
    )
    private val lemmas = listOf("merge", "la", "piață").mapIndexed { index, word ->
        Lemma(LemmaId("ro:$word"), romanian, word, PartOfSpeech.NOUN, meaning = LocalizedText.of(en = word), frequencyRank = index + 1)
    }

    private fun card(word: String, tokenIndex: Int, due: Long) =
        ReviewCard(LemmaId("ro:$word"), romanian, CardContext(text.id, 0, tokenIndex), memory = null, due = due, createdAt = 0)

    private val reviews = FakeReviewRepository()
    private val textToSpeech = FakeTextToSpeech()

    private fun viewModel() = ReviewViewModel(
        SavedStateHandle(),
        FakeUserPreferencesRepository(UserPreferences(learningLanguage = romanian)),
        reviews,
        object : TextRepository {
            override fun observeTexts(languageCode: LanguageCode) = flowOf(listOf(text))

            override fun observeText(id: TextId) = flowOf(text.takeIf { it.id == id })
        },
        object : LexiconRepository {
            override fun observeLemmas(languageCode: LanguageCode) = flowOf(lemmas)
        },
        object : WordStatusRepository {
            override fun observeWords(languageCode: LanguageCode): Flow<List<LearnerWord>> = flowOf(emptyList())

            override suspend fun setStatus(languageCode: LanguageCode, lemmaIds: Collection<LemmaId>, status: WordStatus) = Unit
        },
        object : ReadingLogRepository {
            override fun observeLog(languageCode: LanguageCode): Flow<List<ReadingLogEntry>> = flowOf(emptyList())

            override suspend fun add(languageCode: LanguageCode, entry: ReadingLogEntry) = Unit
        },
        clock,
        textToSpeech,
    )

    @Test
    fun `the session holds the due cards, oldest first, with the word hidden`() = runTest {
        reviews.cards.value = listOf(card("piață", 4, due = now - 10), card("merge", 0, due = now - 50), card("la", 2, due = now + 1))
        val viewModel = viewModel()

        val state = viewModel.state<ReviewUiState.Reviewing>()

        assertEquals(2, state.total)
        assertEquals("merge", state.card.lemma.text)
        assertEquals(0, state.card.tokenIndex)
        assertFalse(state.card.revealed)
    }

    @Test
    fun `answering schedules the card with FSRS and moves on`() = runTest {
        reviews.cards.value = listOf(card("merge", 0, due = now), card("piață", 4, due = now))
        val viewModel = viewModel()
        viewModel.state<ReviewUiState.Reviewing>()

        viewModel.onRevealClick()
        viewModel.onRatingClick(Rating.GOOD)

        val next = viewModel.state<ReviewUiState.Reviewing> { it.position == 1 }
        assertEquals("piață", next.card.lemma.text)
        val saved = reviews.cards.value.first { it.lemmaId == LemmaId("ro:merge") }
        assertEquals(now + 4 * MILLIS_PER_DAY, saved.due)
        assertEquals(listOf(ReviewLogEntry(LemmaId("ro:merge"), Rating.GOOD, now)), reviews.log.value)
    }

    @Test
    fun `a card cannot be rated before its answer is shown`() = runTest {
        reviews.cards.value = listOf(card("merge", 0, due = now))
        val viewModel = viewModel()
        viewModel.state<ReviewUiState.Reviewing>()

        viewModel.onRatingClick(Rating.EASY)

        assertEquals(0, viewModel.state<ReviewUiState.Reviewing>().position)
        assertTrue(reviews.log.value.isEmpty())
    }

    @Test
    fun `the end of the session shows how many were remembered`() = runTest {
        reviews.cards.value = listOf(card("merge", 0, due = now), card("piață", 4, due = now))
        val viewModel = viewModel()
        viewModel.state<ReviewUiState.Reviewing>()

        viewModel.onRevealClick()
        viewModel.onRatingClick(Rating.AGAIN)
        viewModel.state<ReviewUiState.Reviewing> { it.position == 1 }
        viewModel.onRevealClick()
        viewModel.onRatingClick(Rating.GOOD)

        val finished = viewModel.state<ReviewUiState.Finished>()
        assertEquals(2, finished.reviewed)
        assertEquals(50, finished.successRate)
        // "Again" brings "merge" back in 10 minutes.
        assertEquals(600_000L, finished.nextReviewIn)
        assertEquals(text.id, finished.suggestedText?.id)
    }

    @Test
    fun `with nothing due the next review is announced`() = runTest {
        reviews.cards.value = listOf(card("merge", 0, due = now + MILLIS_PER_DAY))

        val state = viewModel().state<ReviewUiState.NothingDue>()

        assertEquals(MILLIS_PER_DAY, state.nextReviewIn)
    }

    @Test
    fun `the sentence can be heard once the answer is shown`() = runTest {
        reviews.cards.value = listOf(card("merge", 0, due = now))
        val viewModel = viewModel()
        viewModel.state<ReviewUiState.Reviewing>()

        viewModel.onRevealClick()
        viewModel.onPlayClick()

        assertEquals(listOf("Merg la piață."), textToSpeech.requests.map { it.text })
    }

    private suspend inline fun <reified T : ReviewUiState> ReviewViewModel.state(crossinline until: (T) -> Boolean = { true }): T =
        uiState.first { it is T && until(it) } as T
}
