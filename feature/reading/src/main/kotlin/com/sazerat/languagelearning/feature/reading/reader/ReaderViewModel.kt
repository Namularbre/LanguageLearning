package com.sazerat.languagelearning.feature.reading.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.model.toStatusMap
import com.sazerat.languagelearning.core.domain.repository.LexiconRepository
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.ReviewRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import com.sazerat.languagelearning.feature.reading.navigation.Reader
import com.sazerat.languagelearning.speech.api.SpeakResult
import com.sazerat.languagelearning.speech.api.SpeechError
import com.sazerat.languagelearning.speech.api.SpeechRate
import com.sazerat.languagelearning.speech.api.TextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** A word of the text: the token [tokenIndex] of the sentence [sentenceIndex]. */
data class WordRef(val sentenceIndex: Int, val tokenIndex: Int)

/** The bottom sheet opened by tapping a word (spec §4). */
data class WordSheetUiState(
    val word: WordRef,
    /** The form as written in the text, e.g. "casei". */
    val form: String,
    val formNote: LocalizedText?,
    val lemma: Lemma,
    val status: WordStatus,
    val sentence: Sentence,
    val showPronunciation: Boolean,
)

/** Shown once the learner says they finished the text. */
data class ReadingSummary(
    /** Words read without being tapped, now known. */
    val newlyKnown: Int,
    /** Words of this text in the reviews. */
    val learning: Int,
)

sealed interface ReaderUiState {
    data object Loading : ReaderUiState

    data object NotFound : ReaderUiState

    data class Success(
        val text: ReadingText,
        /** Words without a status are new. */
        val statuses: Map<LemmaId, WordStatus>,
        val translatedSentences: Set<Int>,
        /** Sentence the player is on: highlighted while playing, resumed from after a pause. */
        val currentSentence: Int,
        val isPlaying: Boolean,
        val rate: SpeechRate,
        val wordSheet: WordSheetUiState? = null,
        /** Shown once in a snackbar, then cleared with [ReaderViewModel.onSpeechErrorShown]. */
        val speechError: SpeechError? = null,
        val summary: ReadingSummary? = null,
    ) : ReaderUiState
}

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    textRepository: TextRepository,
    lexiconRepository: LexiconRepository,
    private val wordStatusRepository: WordStatusRepository,
    private val reviewRepository: ReviewRepository,
    private val readingLogRepository: ReadingLogRepository,
    preferencesRepository: UserPreferencesRepository,
    private val clock: Clock,
    private val textToSpeech: TextToSpeech,
) : ViewModel() {
    private val textId = TextId(checkNotNull(savedStateHandle[Reader.TEXT_ID_KEY]))

    // Choices made on this screen: saved so they survive process death.
    private val openWord = savedStateHandle.getStateFlow<IntArray?>(OPEN_WORD_KEY, null)
    private val translated = savedStateHandle.getStateFlow(TRANSLATED_KEY, intArrayOf())
    private val currentSentence = savedStateHandle.getStateFlow(CURRENT_SENTENCE_KEY, 0)
    private val rate = savedStateHandle.getStateFlow(RATE_KEY, SpeechRate.NORMAL.name)

    private val isPlaying = MutableStateFlow(false)
    private val speechError = MutableStateFlow<SpeechError?>(null)
    private val summary = MutableStateFlow<ReadingSummary?>(null)

    /** Plays a sentence, the rest of the text or a word: one at a time. */
    private var speech: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val content: Flow<ReaderContent?> = textRepository.observeText(textId).flatMapLatest { text ->
        if (text == null) {
            flowOf(null)
        } else {
            combine(
                lexiconRepository.observeLemmas(text.languageCode),
                wordStatusRepository.observeWords(text.languageCode),
                preferencesRepository.preferences,
            ) { lemmas, words, preferences ->
                ReaderContent(text, lemmas.associateBy { it.id }, words.toStatusMap(), preferences.pronunciationHelp)
            }
        }
    }

    private val choices: Flow<ReaderChoices> =
        combine(openWord, translated, currentSentence, rate) { open, translated, current, rate ->
            ReaderChoices(
                openWord = open?.let { WordRef(it[0], it[1]) },
                translatedSentences = translated.toSet(),
                currentSentence = current,
                rate = SpeechRate.valueOf(rate),
            )
        }

    val uiState: StateFlow<ReaderUiState> = combine(
        content,
        choices,
        isPlaying,
        speechError,
        summary,
    ) { content, choices, playing, error, summary ->
        if (content == null) {
            ReaderUiState.NotFound
        } else {
            ReaderUiState.Success(
                text = content.text,
                statuses = content.statuses,
                translatedSentences = choices.translatedSentences,
                currentSentence = choices.currentSentence,
                isPlaying = playing,
                rate = choices.rate,
                wordSheet = choices.openWord?.let { content.wordSheet(it) },
                speechError = error,
                summary = summary,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReaderUiState.Loading,
    )

    private val success: ReaderUiState.Success? get() = uiState.value as? ReaderUiState.Success

    fun onPlayPauseClick() {
        val state = success ?: return
        if (state.isPlaying) {
            stopSpeech()
        } else {
            playSentences(state.text, from = state.currentSentence, toTheEnd = true)
        }
    }

    fun onRepeatSentenceClick() {
        val state = success ?: return
        playSentences(state.text, from = state.currentSentence, toTheEnd = false)
    }

    fun onRateSelected(rate: SpeechRate) {
        // Takes effect from the next sentence: cutting the current one would be jarring.
        savedStateHandle[RATE_KEY] = rate.name
    }

    /** Long press on a sentence: shows or hides its translation. */
    fun onSentenceLongPress(sentenceIndex: Int) {
        val current = translated.value.toSet()
        savedStateHandle[TRANSLATED_KEY] =
            (if (sentenceIndex in current) current - sentenceIndex else current + sentenceIndex).toIntArray()
    }

    fun onWordClick(word: WordRef) {
        val state = success ?: return
        val lemmaId = state.text.sentences.getOrNull(word.sentenceIndex)?.tokens?.getOrNull(word.tokenIndex)?.lemmaId ?: return
        // Reading pauses while the learner looks at a word.
        stopSpeech()
        val touched = savedStateHandle.get<ArrayList<String>>(TOUCHED_KEY) ?: arrayListOf()
        if (lemmaId.value !in touched) touched.add(lemmaId.value)
        savedStateHandle[TOUCHED_KEY] = touched
        savedStateHandle[OPEN_WORD_KEY] = intArrayOf(word.sentenceIndex, word.tokenIndex)
    }

    fun onWordSheetDismiss() {
        savedStateHandle[OPEN_WORD_KEY] = null
    }

    fun onPlayWordClick() {
        val state = success ?: return
        val sheet = state.wordSheet ?: return
        launchSpeech { speak(sheet.form, state) }
    }

    /** Adds the open word to the reviews, with the sentence it was met in (spec §5.2). */
    fun onAddToReviewClick() {
        val state = success ?: return
        val sheet = state.wordSheet ?: return
        onWordSheetDismiss()
        viewModelScope.launch {
            val now = clock.now()
            wordStatusRepository.setStatus(state.text.languageCode, listOf(sheet.lemma.id), WordStatus.LEARNING)
            reviewRepository.addCard(
                ReviewCard(
                    lemmaId = sheet.lemma.id,
                    languageCode = state.text.languageCode,
                    context = CardContext(state.text.id, sheet.word.sentenceIndex, sheet.word.tokenIndex),
                    memory = null,
                    due = now,
                    createdAt = now,
                ),
            )
        }
    }

    fun onKnownClick() {
        val state = success ?: return
        val sheet = state.wordSheet ?: return
        onWordSheetDismiss()
        viewModelScope.launch {
            wordStatusRepository.setStatus(state.text.languageCode, listOf(sheet.lemma.id), WordStatus.KNOWN)
        }
    }

    /**
     * "I'm done reading" (spec §5.2): every new word the learner did not tap is now known, and the
     * text counts towards the daily goal.
     */
    fun onFinishClick() {
        val state = success ?: return
        if (state.summary != null) return
        stopSpeech()
        val touched = savedStateHandle.get<ArrayList<String>>(TOUCHED_KEY).orEmpty().map(::LemmaId).toSet()
        val words = state.text.wordLemmas.toSet()
        val newlyKnown = words.filter { it !in touched && (state.statuses[it] ?: WordStatus.NEW) == WordStatus.NEW }
        summary.value = ReadingSummary(
            newlyKnown = newlyKnown.size,
            learning = words.count { state.statuses[it] == WordStatus.LEARNING },
        )
        viewModelScope.launch {
            wordStatusRepository.setStatus(state.text.languageCode, newlyKnown, WordStatus.KNOWN)
            readingLogRepository.add(state.text.languageCode, ReadingLogEntry(state.text.id, state.text.minutes, clock.now()))
        }
    }

    /**
     * Starts the text over from the end-of-text summary. Words met again keep their status: only a
     * word that is still new and left untouched becomes known when the text is finished again.
     */
    fun onReadAgainClick() {
        stopSpeech()
        savedStateHandle[TOUCHED_KEY] = arrayListOf<String>()
        savedStateHandle[CURRENT_SENTENCE_KEY] = 0
        savedStateHandle[TRANSLATED_KEY] = intArrayOf()
        savedStateHandle[OPEN_WORD_KEY] = null
        summary.value = null
    }

    fun onSpeechErrorShown() {
        speechError.value = null
    }

    private fun playSentences(text: ReadingText, from: Int, toTheEnd: Boolean) {
        val state = success ?: return
        launchSpeech {
            var index = from.coerceIn(0, text.sentences.lastIndex)
            while (true) {
                savedStateHandle[CURRENT_SENTENCE_KEY] = index
                if (!speak(text.sentences[index].text, state)) return@launchSpeech
                if (!toTheEnd) return@launchSpeech
                if (index == text.sentences.lastIndex) break
                index++
            }
            // Read to the end: the next "play" starts again from the top.
            savedStateHandle[CURRENT_SENTENCE_KEY] = 0
        }
    }

    /** Speaks at the current rate. Returns `false`, and reports the error, if speech failed. */
    private suspend fun speak(text: String, state: ReaderUiState.Success): Boolean {
        val result = textToSpeech.speak(text, state.text.languageCode, SpeechRate.valueOf(rate.value))
        if (result is SpeakResult.Failed) speechError.value = result.error
        return result is SpeakResult.Done
    }

    private fun launchSpeech(block: suspend () -> Unit) {
        val previous = speech
        speech = viewModelScope.launch {
            // Stop what is playing before starting, so two never overlap.
            previous?.cancelAndJoin()
            isPlaying.value = true
            try {
                block()
            } finally {
                isPlaying.value = false
            }
        }
    }

    private fun stopSpeech() {
        // The job is kept: the next launch waits for it to be fully stopped.
        speech?.cancel()
        isPlaying.value = false
    }

    private data class ReaderContent(
        val text: ReadingText,
        val lemmas: Map<LemmaId, Lemma>,
        val statuses: Map<LemmaId, WordStatus>,
        val pronunciationHelp: Boolean,
    ) {
        fun wordSheet(word: WordRef): WordSheetUiState? {
            val sentence = text.sentences.getOrNull(word.sentenceIndex) ?: return null
            val token = sentence.tokens.getOrNull(word.tokenIndex) ?: return null
            val lemma = token.lemmaId?.let(lemmas::get) ?: return null
            return WordSheetUiState(
                word = word,
                form = token.text,
                formNote = token.formNote,
                lemma = lemma,
                status = statuses[lemma.id] ?: WordStatus.NEW,
                sentence = sentence,
                showPronunciation = pronunciationHelp,
            )
        }
    }

    private data class ReaderChoices(
        val openWord: WordRef?,
        val translatedSentences: Set<Int>,
        val currentSentence: Int,
        val rate: SpeechRate,
    )

    private companion object {
        const val OPEN_WORD_KEY = "openWord"
        const val TRANSLATED_KEY = "translatedSentences"
        const val CURRENT_SENTENCE_KEY = "currentSentence"
        const val RATE_KEY = "rate"

        /** Lemmas tapped during this reading: they are not marked known when the text is finished. */
        const val TOUCHED_KEY = "touchedLemmas"
    }
}
