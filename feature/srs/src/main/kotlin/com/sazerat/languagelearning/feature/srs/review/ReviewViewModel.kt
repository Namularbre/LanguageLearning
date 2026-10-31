package com.sazerat.languagelearning.feature.srs.review

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
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
import com.sazerat.languagelearning.core.domain.review.ExerciseKind
import com.sazerat.languagelearning.core.domain.review.FsrsScheduler
import com.sazerat.languagelearning.core.domain.review.MultipleChoice
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
import kotlin.random.Random

/** The text to suggest at the end, or when nothing is due. */
data class SuggestedText(val id: TextId, val title: String)

/**
 * A multiple-choice question: the learner picks an answer and the FSRS rating follows from it.
 * The right answer is [ReviewCardUiState.lemma].
 */
data class ExerciseUiState(
    val kind: ExerciseKind,
    /** In the order shown. */
    val choices: List<Lemma>,
    /** The answer picked, `null` until then. */
    val selected: LemmaId?,
) {
    val answered: Boolean get() = selected != null
}

/** One card of the session: the front is the sentence the word was met in (spec §4). */
data class ReviewCardUiState(
    val lemma: Lemma,
    val sentence: Sentence,
    /** The token asked about on the front. */
    val tokenIndex: Int,
    val revealed: Boolean,
    /** Time until the next review for each answer, in milliseconds. */
    val intervals: Map<Rating, Long>,
    val showPronunciation: Boolean,
    /**
     * The question to answer. `null` when there are too few words to offer choices: the learner
     * then shows the answer and rates the card with the four FSRS buttons.
     */
    val exercise: ExerciseUiState? = null,
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
 *
 * Each card is a multiple-choice question ([MultipleChoice]) whenever the lexicon offers enough
 * wrong answers; the answer then gives the FSRS rating. Otherwise the learner rates it.
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

    // The question of the current card, drawn once so that it survives process death unchanged.
    private val exercise: Flow<SavedExercise?> = combine(
        savedStateHandle.getStateFlow(EXERCISE_POSITION_KEY, -1),
        savedStateHandle.getStateFlow<String?>(EXERCISE_KIND_KEY, null),
        savedStateHandle.getStateFlow<ArrayList<String>?>(CHOICES_KEY, null),
        savedStateHandle.getStateFlow<String?>(SELECTED_KEY, null),
    ) { position, kind, choices, selected ->
        SavedExercise(
            position = position,
            kind = kind?.let { ExerciseKind.valueOf(it) },
            choices = choices.orEmpty().map(::LemmaId),
            selected = selected?.let(::LemmaId),
        ).takeIf { position >= 0 }
    }

    // Set while the next question is drawn, so that a double tap does not skip a card.
    private var advancing = false

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
                met = words.map { it.lemmaId }.toSet() + cards.map { it.lemmaId },
                suggestedText = suggested?.let { SuggestedText(it.id, it.title) },
                showPronunciation = preferences.pronunciationHelp,
            )
        }
    }

    private val session: Flow<Session?> = combine(queue, position, revealed, remembered, exercise) { queue, position, revealed, remembered, exercise ->
        queue?.let { Session(it.map(::LemmaId), position, revealed, remembered, exercise) }
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
            // The question of this card is still being drawn.
            session.exercise?.position != session.position -> ReviewUiState.Loading
            else -> {
                val card = content.cardUiState(session.queue[session.position], session.revealed, session.exercise, now)
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
                prepareExercise(due.map { it.lemmaId }, 0)
                savedStateHandle[QUEUE_KEY] = ArrayList(due.map { it.lemmaId.value })
            }
        } else if (savedStateHandle.get<Int>(EXERCISE_POSITION_KEY) != position.value) {
            viewModelScope.launch { prepareExercise(queue.value.orEmpty().map(::LemmaId), position.value) }
        }
    }

    /** Draws the question of the card at [index] of [queue], before that card is shown. */
    private suspend fun prepareExercise(queue: List<LemmaId>, index: Int) {
        val content = content.first()
        val lemmaId = queue.getOrNull(index)
        val card = lemmaId?.let { content.cards[it] }
        val lemma = lemmaId?.let { content.lemmas[it] }
        val choices = if (card != null && lemma != null) {
            MultipleChoice.choices(lemma, content.lemmas.values.toList(), content.met - lemma.id, Random.Default)
        } else {
            null
        }
        val kind = card?.let(MultipleChoice::kindFor)?.takeIf { choices != null }
        savedStateHandle[EXERCISE_KIND_KEY] = kind?.name
        savedStateHandle[CHOICES_KEY] = choices?.let { ArrayList(it.map(LemmaId::value)) }
        savedStateHandle[SELECTED_KEY] = null
        savedStateHandle[SHOWN_AT_KEY] = clock.now()
        savedStateHandle[EXERCISE_POSITION_KEY] = index
    }

    /** Moves on to the next card once its question is ready. */
    private fun advance(from: Int) {
        if (advancing) return
        advancing = true
        viewModelScope.launch {
            prepareExercise(queue.value.orEmpty().map(::LemmaId), from + 1)
            savedStateHandle[REVEALED_KEY] = false
            savedStateHandle[POSITION_KEY] = from + 1
            advancing = false
        }
    }

    /** Saves the answer in the background and counts it for the end of the session. */
    private fun saveAnswer(lemmaId: LemmaId, languageCode: LanguageCode, rating: Rating, now: Long) {
        if (rating != Rating.AGAIN) savedStateHandle[REMEMBERED_KEY] = remembered.value + 1
        viewModelScope.launch {
            val card = reviewRepository.observeCards(languageCode).first().find { it.lemmaId == lemmaId } ?: return@launch
            reviewRepository.saveReview(scheduler.review(card, rating, now), ReviewLogEntry(card.lemmaId, rating, now))
        }
    }

    /** Turns over a card that has no question. */
    fun onRevealClick() {
        if ((uiState.value as? ReviewUiState.Reviewing)?.card?.exercise != null) return
        savedStateHandle[REVEALED_KEY] = true
    }

    fun onPlayClick() {
        val card = (uiState.value as? ReviewUiState.Reviewing)?.card ?: return
        player.play(card.sentence.text, card.lemma.languageCode, SpeechRate.NORMAL)
    }

    /** Rates a card that has no question, once its answer is shown. */
    fun onRatingClick(rating: Rating) {
        val state = uiState.value as? ReviewUiState.Reviewing ?: return
        if (!state.card.revealed || state.card.exercise != null || advancing) return
        saveAnswer(state.card.lemma.id, state.card.lemma.languageCode, rating, clock.now())
        advance(state.position)
    }

    /**
     * Answers the question: the card is turned over at once, and its rating follows from the answer
     * and the time taken (see [MultipleChoice.rating]).
     */
    fun onChoiceClick(choice: LemmaId) {
        val state = uiState.value as? ReviewUiState.Reviewing ?: return
        val exercise = state.card.exercise ?: return
        if (exercise.answered || exercise.choices.none { it.id == choice }) return
        val now = clock.now()
        val shownAt = savedStateHandle.get<Long>(SHOWN_AT_KEY) ?: now
        val rating = MultipleChoice.rating(correct = choice == state.card.lemma.id, elapsedMillis = now - shownAt)
        savedStateHandle[SELECTED_KEY] = choice.value
        savedStateHandle[REVEALED_KEY] = true
        saveAnswer(state.card.lemma.id, state.card.lemma.languageCode, rating, now)
    }

    /** Leaves an answered question for the next card. */
    fun onContinueClick() {
        val state = uiState.value as? ReviewUiState.Reviewing ?: return
        if (state.card.exercise?.answered != true) return
        advance(state.position)
    }

    fun onSpeechErrorShown() {
        player.clearError()
    }

    private data class Session(
        val queue: List<LemmaId>,
        val position: Int,
        val revealed: Boolean,
        val remembered: Int,
        val exercise: SavedExercise?,
    )

    /** The question drawn for the card at [position]; no [kind] when the card has no question. */
    private data class SavedExercise(val position: Int, val kind: ExerciseKind?, val choices: List<LemmaId>, val selected: LemmaId?)

    private inner class ReviewContent(
        val cards: Map<LemmaId, ReviewCard>,
        val texts: Map<TextId, ReadingText>,
        val lemmas: Map<LemmaId, Lemma>,
        /** Words the learner has met: they make better wrong answers. */
        val met: Set<LemmaId>,
        val suggestedText: SuggestedText?,
        val showPronunciation: Boolean,
    ) {
        fun cardUiState(lemmaId: LemmaId, revealed: Boolean, exercise: SavedExercise?, now: Long): ReviewCardUiState? {
            val card = cards[lemmaId] ?: return null
            val lemma = lemmas[lemmaId] ?: return null
            val sentence = texts[card.context.textId]?.sentences?.getOrNull(card.context.sentenceIndex) ?: return null
            val question = exercise?.kind?.let { kind ->
                val choices = exercise.choices.map { lemmas[it] ?: return null }
                ExerciseUiState(kind, choices, exercise.selected)
            }
            return ReviewCardUiState(
                lemma = lemma,
                sentence = sentence,
                tokenIndex = card.context.tokenIndex,
                revealed = revealed,
                intervals = scheduler.nextIntervals(card, now),
                showPronunciation = showPronunciation,
                exercise = question,
            )
        }
    }

    private companion object {
        const val QUEUE_KEY = "queue"
        const val POSITION_KEY = "position"
        const val REVEALED_KEY = "revealed"
        const val REMEMBERED_KEY = "remembered"
        const val EXERCISE_POSITION_KEY = "exercisePosition"
        const val EXERCISE_KIND_KEY = "exerciseKind"
        const val CHOICES_KEY = "choices"
        const val SELECTED_KEY = "selected"
        const val SHOWN_AT_KEY = "shownAt"
    }
}
