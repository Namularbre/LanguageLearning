package com.sazerat.languagelearning.feature.session.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.domain.model.toStatusMap
import com.sazerat.languagelearning.core.domain.progress.Activity
import com.sazerat.languagelearning.core.domain.progress.SECONDS_PER_REVIEW
import com.sazerat.languagelearning.core.domain.progress.activeDays
import com.sazerat.languagelearning.core.domain.progress.minutesOn
import com.sazerat.languagelearning.core.domain.progress.newWordsThisWeek
import com.sazerat.languagelearning.core.domain.progress.streak
import com.sazerat.languagelearning.core.domain.reading.comprehension
import com.sazerat.languagelearning.core.domain.reading.suggestNextText
import com.sazerat.languagelearning.core.domain.reading.suggestReread
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.ReviewRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** The "Read next" card. */
data class NextText(
    val id: TextId,
    val title: String,
    val level: CefrLevel,
    val kind: TextKind,
    val minutes: Int,
    val comprehension: Int,
    /** Already read: suggested again because every text has been read. */
    val isReread: Boolean = false,
)

sealed interface TodayUiState {
    data object Loading : TodayUiState

    data class Success(
        val streak: Int,
        val minutesToday: Int,
        val goalMinutes: Int,
        val dueCards: Int,
        /** Estimated minutes to review the due cards, at least 1 when there are some. */
        val reviewMinutes: Int,
        /** Once every text has been read, the one read longest ago, to read again. */
        val nextText: NextText?,
        val newWordsThisWeek: Int,
    ) : TodayUiState
}

/** What to do today (spec §4): review what is due, then read the suggested text. */
@HiltViewModel
class TodayViewModel @Inject constructor(
    preferencesRepository: UserPreferencesRepository,
    reviewRepository: ReviewRepository,
    readingLogRepository: ReadingLogRepository,
    wordStatusRepository: WordStatusRepository,
    textRepository: TextRepository,
    clock: Clock,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TodayUiState> = preferencesRepository.preferences.flatMapLatest { preferences ->
        val language = preferences.learningLanguageOrDefault
        combine(
            reviewRepository.observeCards(language),
            reviewRepository.observeLog(language),
            readingLogRepository.observeLog(language),
            wordStatusRepository.observeWords(language),
            textRepository.observeTexts(language),
        ) { cards, reviews, readings, words, texts ->
            val now = clock.now()
            val today = clock.dayOf(now)
            val activity = Activity(reviews, readings)
            val statuses = words.toStatusMap()
            val due = cards.count { it.isDue(now) }
            TodayUiState.Success(
                streak = streak(activity.activeDays(clock), today),
                minutesToday = activity.minutesOn(today, clock),
                goalMinutes = preferences.dailyGoal.minutes,
                dueCards = due,
                reviewMinutes = (due * SECONDS_PER_REVIEW + 59) / 60,
                nextText = suggestNextText(texts, statuses, readings.map { it.textId }.toSet())?.let { text ->
                    NextText(text.id, text.title, text.level, text.kind, text.minutes, text.comprehension(statuses))
                } ?: suggestReread(texts, readings)?.let { text ->
                    NextText(text.id, text.title, text.level, text.kind, text.minutes, text.comprehension(statuses), isReread = true)
                },
                newWordsThisWeek = newWordsThisWeek(words, today, clock),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TodayUiState.Loading,
    )
}
