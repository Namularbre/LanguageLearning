package com.sazerat.languagelearning.feature.srs.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.Lemma
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.toStatusMap
import com.sazerat.languagelearning.core.domain.reading.suggestNextText
import com.sazerat.languagelearning.core.domain.repository.LexiconRepository
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.ReviewRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import com.sazerat.languagelearning.core.domain.review.FsrsScheduler
import com.sazerat.languagelearning.speech.api.SpeechError
import com.sazerat.languagelearning.speech.api.SpeechPlayer
import com.sazerat.languagelearning.speech.api.SpeechRate
import com.sazerat.languagelearning.speech.api.TextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** The text to suggest at the end, or when nothing is due. */
data class SuggestedText(val id: TextId, val title: String)

/** One card of the session: the front is the sentence with the word hidden (spec §4). */
data class ReviewCardUiState(
    val lemma: Lemma,
    val sentence: Sentence,
    /** The token hidden on the front. */
    val tokenIndex: Int,
    val revealed: Boolean,
    /** Time until the next review for each answer, in milliseconds. */
    val intervals: Map<Rating, Long>,
    val showPronunciation: Boolean,
)

sealed interface ReviewUiState {
    data object Loading : ReviewUiState

    /** Nothing due when the session started. */
    data class NothingDue(
        /** In milliseconds from now; `null` when the learner has no card at all. */
        val nextReviewIn: Long?,
        val suggestedText: SuggestedText?,
    ) : ReviewUiState

    data class Reviewing(
        val card: ReviewCardUiState,
        /** 0-based index of this card in the session. */
        val position: Int,
        val total: Int,
        val isPlaying: Boolean = false,
        /** Shown once in a snackbar, then cleared with [ReviewViewModel.onSpeechErrorShown]. */
        val speechError: SpeechError? = null,
    ) : ReviewUiState

    data class Finished(
        val reviewed: Int,
        /** Share of the answers other than "Again", 0 to 100. */
        val successRate: Int,
        val nextReviewIn: Long?,
        val suggestedText: SuggestedText?,
    ) : ReviewUiState
}

/**
 * A review session: the cards due when it starts, in the order they fell due. A card answered
 * "Again" comes back in 10 minutes, in the next session, rather than at the end of this one.
 */
@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    preferencesRepository: UserPreferencesRepository,
    private val reviewRepository: ReviewRepository,
    textRepository: TextRepository,
    lexiconRepository: LexiconRepository,
    wordStatusRepository: WordStatusRepository,
    readingLogRepository: ReadingLogRepository,
    private val clock: Clock,
    textToSpeech: TextToSpeech,
) : ViewModel() {
    private val scheduler = FsrsScheduler()
    private val player = SpeechPlayer(textToSpeech, viewModelScope)

    // The session is saved, so process death resumes it on the same card.
    private val queue = savedStateHandle.getStateFlow<ArrayList<String>?>(QUEUE_KEY, null)
    private val position = savedStateHandle.getStateFlow(POSITION_KEY, 0)
    private val revealed = savedStateHandle.getStateFlow(REVEALED_KEY, false)
    private val remembered = savedStateHandle.getStateFlow(REMEMBERED_KEY, 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val content: Flow<ReviewContent> = preferencesRepository.preferences.flatMapLatest { preferences ->
        val language = preferences.learningLanguageOrDefault
        combine(
            reviewRepository.observeCards(language),
            textRepository.observeTexts(language),
            lexiconRepository.observeLemmas(language),
            wordStatusRepository.observeWords(language),
            readingLogRepository.observeLog(language),
        ) { cards, texts, lemmas, words, log ->
            val suggested = suggestNextText(texts, words.toStatusMap(), log.map { it.textId }.toSet())
            ReviewContent(
                cards = cards.associateBy { it.lemmaId },
                texts = texts.associateBy { it.id },
                lemmas = lemmas.associateBy { it.id },
                suggestedText = suggested?.let { SuggestedText(it.id, it.title) },
                showPronunciation = preferences.pronunciationHelp,
            )
        }
    }

    private val session: Flow<Session?> = combine(queue, position, revealed, remembered) { queue, position, revealed, remembered ->
        queue?.let { Session(it.map(::LemmaId), position, revealed, remembered) }
    }

    val uiState: StateFlow<ReviewUiState> = combine(
        content,
        session,
        player.playingText,
        player.error,
    ) { content, session, playing, error ->
        val now = clock.now()
        val nextReviewIn = content.cards.values.minOfOrNull { it.due }?.let { (it - now).coerceAtLeast(0) }
        when {
            session == null -> ReviewUiState.Loading
            session.queue.isEmpty() -> ReviewUiState.NothingDue(nextReviewIn, content.suggestedText)
            session.position >= session.queue.size -> ReviewUiState.Finished(
                reviewed = session.queue.size,
                successRate = session.remembered * 100 / session.queue.size,
                nextReviewIn = nextReviewIn,
                suggestedText = content.suggestedText,
            )
            else -> {
                val card = content.cardUiState(session.queue[session.position], session.revealed, now)
                if (card == null) {
                    ReviewUiState.Loading
                } else {
                    ReviewUiState.Reviewing(card, session.position, session.queue.size, playing != null, error)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ReviewUiState.Loading,
    )

    init {
        if (queue.value == null) {
            viewModelScope.launch {
                val language = preferencesRepository.preferences.first().learningLanguageOrDefault
                val now = clock.now()
                val due = reviewRepository.observeCards(language).first().filter { it.isDue(now) }.sortedBy { it.due }
                savedStateHandle[QUEUE_KEY] = ArrayList(due.map { it.lemmaId.value })
            }
        }
    }

    fun onRevealClick() {
        savedStateHandle[REVEALED_KEY] = true
    }

    fun onPlayClick() {
        val card = (uiState.value as? ReviewUiState.Reviewing)?.card ?: return
        player.play(card.sentence.text, card.lemma.languageCode, SpeechRate.NORMAL)
    }

    fun onRatingClick(rating: Rating) {
        val state = uiState.value as? ReviewUiState.Reviewing ?: return
        if (!state.card.revealed) return
        val now = clock.now()
        // Moves on at once; the answer is saved in the background.
        savedStateHandle[POSITION_KEY] = state.position + 1
        savedStateHandle[REVEALED_KEY] = false
        if (rating != Rating.AGAIN) savedStateHandle[REMEMBERED_KEY] = remembered.value + 1
        viewModelScope.launch {
            val card = reviewRepository.observeCards(state.card.lemma.languageCode).first()
                .find { it.lemmaId == state.card.lemma.id } ?: return@launch
            reviewRepository.saveReview(scheduler.review(card, rating, now), ReviewLogEntry(card.lemmaId, rating, now))
        }
    }

    fun onSpeechErrorShown() {
        player.clearError()
    }

    private data class Session(val queue: List<LemmaId>, val position: Int, val revealed: Boolean, val remembered: Int)

    private inner class ReviewContent(
        val cards: Map<LemmaId, ReviewCard>,
        val texts: Map<TextId, ReadingText>,
        val lemmas: Map<LemmaId, Lemma>,
        val suggestedText: SuggestedText?,
        val showPronunciation: Boolean,
    ) {
        fun cardUiState(lemmaId: LemmaId, revealed: Boolean, now: Long): ReviewCardUiState? {
            val card = cards[lemmaId] ?: return null
            val lemma = lemmas[lemmaId] ?: return null
            val sentence = texts[card.context.textId]?.sentences?.getOrNull(card.context.sentenceIndex) ?: return null
            return ReviewCardUiState(
                lemma = lemma,
                sentence = sentence,
                tokenIndex = card.context.tokenIndex,
                revealed = revealed,
                intervals = scheduler.nextIntervals(card, now),
                showPronunciation = showPronunciation,
            )
        }
    }

    private companion object {
        const val QUEUE_KEY = "queue"
        const val POSITION_KEY = "position"
        const val REVEALED_KEY = "revealed"
        const val REMEMBERED_KEY = "remembered"
    }
}
