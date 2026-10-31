package com.sazerat.languagelearning.feature.reading

import androidx.lifecycle.SavedStateHandle
import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import com.sazerat.languagelearning.feature.reading.navigation.Reader
import com.sazerat.languagelearning.feature.reading.reader.ReaderUiState
import com.sazerat.languagelearning.feature.reading.reader.ReaderViewModel
import com.sazerat.languagelearning.feature.reading.reader.ReadingSummary
import com.sazerat.languagelearning.feature.reading.reader.WordRef
import com.sazerat.languagelearning.speech.api.SpeakResult
import com.sazerat.languagelearning.speech.api.SpeechError
import com.sazerat.languagelearning.speech.api.SpeechRate
import com.sazerat.languagelearning.speech.fake.FakeTextToSpeech
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReaderViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val words = FakeWordStatusRepository()
    private val reviews = FakeReviewRepository()
    private val log = FakeReadingLogRepository()
    private val preferences = FakeUserPreferencesRepository(UserPreferences(learningLanguage = romanian))
    private val clock = FakeClock(now = 5_000)
    private val textToSpeech = FakeTextToSpeech()

    // "Eu merg la piață." — token 6 is "piață"; "Merg mult." — token 2 is "mult".
    private val piata = WordRef(sentenceIndex = 0, tokenIndex = 6)
    private val mult = WordRef(sentenceIndex = 1, tokenIndex = 2)

    private fun viewModel(textId: String = previewText.id.value) = ReaderViewModel(
        SavedStateHandle(mapOf(Reader.TEXT_ID_KEY to textId)),
        FakeTextRepository(listOf(previewText)),
        FakeLexiconRepository(previewLemmas),
        words,
        reviews,
        log,
        preferences,
        clock,
        textToSpeech,
    )

    @Test
    fun `an unknown text is reported as not found`() = runTest {
        val state = viewModel("missing").uiState.first { it !is ReaderUiState.Loading }

        assertEquals(ReaderUiState.NotFound, state)
    }

    @Test
    fun `tapping a word opens its sheet with the base form and the sentence`() = runTest {
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onWordClick(WordRef(sentenceIndex = 0, tokenIndex = 2))

        val sheet = viewModel.successState().wordSheet!!
        assertEquals("merg", sheet.form)
        assertEquals("a merge", sheet.lemma.text)
        assertEquals(WordStatus.NEW, sheet.status)
        assertEquals(previewText.sentences[0], sheet.sentence)
        assertFalse(sheet.showPronunciation)
    }

    @Test
    fun `adding a word to review makes it learning with a card due now`() = runTest {
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onWordClick(piata)
        viewModel.onAddToReviewClick()

        assertEquals(WordStatus.LEARNING, viewModel.successState().statuses[LemmaId("ro:piață")])
        assertNull(viewModel.successState().wordSheet)
        val card = reviews.cards.value.single()
        assertEquals(CardContext(previewText.id, 0, 6), card.context)
        assertEquals(5_000, card.due)
    }

    @Test
    fun `I already know it marks the word as known`() = runTest {
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onWordClick(mult)
        viewModel.onKnownClick()

        assertEquals(WordStatus.KNOWN, words.statuses.value[LemmaId("ro:mult")])
        assertTrue(reviews.cards.value.isEmpty())
    }

    @Test
    fun `finishing marks untouched new words as known and logs the reading`() = runTest {
        words.setStatus(romanian, listOf(LemmaId("ro:eu")), WordStatus.LEARNING)
        val viewModel = viewModel()
        viewModel.successState()

        // Tapped, then closed without a choice: stays new.
        viewModel.onWordClick(piata)
        viewModel.onWordSheetDismiss()
        viewModel.onFinishClick()

        val statuses = words.statuses.value
        assertEquals(WordStatus.LEARNING, statuses[LemmaId("ro:eu")])
        assertNull(statuses[LemmaId("ro:piață")])
        assertEquals(WordStatus.KNOWN, statuses[LemmaId("ro:merge")])
        assertEquals(WordStatus.KNOWN, statuses[LemmaId("ro:la")])
        assertEquals(WordStatus.KNOWN, statuses[LemmaId("ro:mult")])
        assertEquals(ReadingSummary(newlyKnown = 3, learning = 1), viewModel.successState().summary)
        assertEquals(listOf(ReadingLogEntry(previewText.id, previewText.minutes, 5_000)), log.entries.value)
    }

    @Test
    fun `play reads every sentence in turn at the chosen speed`() = runTest {
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onRateSelected(SpeechRate.SLOW)
        viewModel.onPlayPauseClick()

        assertEquals(
            listOf(
                FakeTextToSpeech.Request("Eu merg la piață.", romanian, SpeechRate.SLOW),
                FakeTextToSpeech.Request("Merg mult.", romanian, SpeechRate.SLOW),
            ),
            textToSpeech.requests,
        )
        // Read to the end: the next play starts again from the top.
        assertEquals(0, viewModel.successState().currentSentence)
        assertFalse(viewModel.successState().isPlaying)
    }

    @Test
    fun `pause stops on the current sentence and play resumes from it`() = runTest {
        val gate = CompletableDeferred<Unit>()
        textToSpeech.gate = gate
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onPlayPauseClick()
        assertTrue(viewModel.successState().isPlaying)
        viewModel.onPlayPauseClick()

        assertFalse(viewModel.successState().isPlaying)
        assertEquals(0, viewModel.successState().currentSentence)
        assertEquals(1, textToSpeech.requests.size)
    }

    @Test
    fun `repeat reads only the current sentence`() = runTest {
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onRepeatSentenceClick()

        assertEquals(listOf("Eu merg la piață."), textToSpeech.requests.map { it.text })
    }

    @Test
    fun `a long press toggles the sentence translation`() = runTest {
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onSentenceLongPress(1)
        assertEquals(setOf(1), viewModel.successState().translatedSentences)

        viewModel.onSentenceLongPress(1)
        assertEquals(emptySet(), viewModel.successState().translatedSentences)
    }

    @Test
    fun `a missing voice stops playback and is reported once`() = runTest {
        textToSpeech.result = SpeakResult.Failed(SpeechError.UNSUPPORTED_LANGUAGE)
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onPlayPauseClick()

        assertEquals(1, textToSpeech.requests.size)
        assertEquals(SpeechError.UNSUPPORTED_LANGUAGE, viewModel.successState().speechError)
        viewModel.onSpeechErrorShown()
        assertNull(viewModel.successState().speechError)
    }

    @Test
    fun `the pronunciation help adds the IPA to the sheet`() = runTest {
        preferences.setPronunciationHelp(true)
        val viewModel = viewModel()
        viewModel.successState()

        viewModel.onWordClick(WordRef(sentenceIndex = 1, tokenIndex = 0))

        val sheet = viewModel.successState().wordSheet!!
        assertTrue(sheet.showPronunciation)
        assertEquals("ˈmer.dʒe", sheet.lemma.ipa)
    }

    private suspend fun ReaderViewModel.successState(): ReaderUiState.Success =
        uiState.first { it is ReaderUiState.Success } as ReaderUiState.Success
}
