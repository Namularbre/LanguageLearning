package com.sazerat.languagelearning.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.model.toStatusMap
import com.sazerat.languagelearning.core.domain.progress.Activity
import com.sazerat.languagelearning.core.domain.progress.GlobalProgress
import com.sazerat.languagelearning.core.domain.progress.LevelComprehension
import com.sazerat.languagelearning.core.domain.progress.StudiedLanguages
import com.sazerat.languagelearning.core.domain.progress.activeDays
import com.sazerat.languagelearning.core.domain.progress.globalProgress
import com.sazerat.languagelearning.core.domain.progress.knownWordsByDay
import com.sazerat.languagelearning.core.domain.progress.levelComprehension
import com.sazerat.languagelearning.core.domain.progress.observeLanguageSummaries
import com.sazerat.languagelearning.core.domain.progress.streak
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
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProgressUiState {
    data object Loading : ProgressUiState

    data class Success(
        val languageCode: LanguageCode,
        val knownWords: Int,
        val learningWords: Int,
        /** Known words at the end of each of the last [CHART_DAYS] days, oldest first. */
        val knownWordsByDay: List<Int>,
        /** Whether something was done on each of the last [CALENDAR_DAYS] days, oldest first. */
        val activeDays: List<Boolean>,
        val streak: Int,
        val levelComprehension: LevelComprehension?,
        /** Every studied language together, above the details of the current one. */
        val global: GlobalProgress,
    ) : ProgressUiState

    companion object {
        const val CHART_DAYS = 30
        const val CALENDAR_DAYS = 35
    }
}

/** Statistics of every language, then of the language being learnt (spec §4 and §8.2). */
@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    wordStatusRepository: WordStatusRepository,
    reviewRepository: ReviewRepository,
    readingLogRepository: ReadingLogRepository,
    textRepository: TextRepository,
    clock: Clock,
) : ViewModel() {
    private val summaries =
        observeLanguageSummaries(StudiedLanguages, wordStatusRepository, reviewRepository, readingLogRepository, clock)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ProgressUiState> = preferencesRepository.preferences.flatMapLatest { preferences ->
        val language = preferences.learningLanguageOrDefault
        combine(
            wordStatusRepository.observeWords(language),
            reviewRepository.observeLog(language),
            readingLogRepository.observeLog(language),
            textRepository.observeTexts(language),
            summaries,
        ) { words, reviews, readings, texts, summaries ->
            val today = clock.today()
            val active = Activity(reviews, readings).activeDays(clock)
            ProgressUiState.Success(
                languageCode = language,
                knownWords = words.count { it.status == WordStatus.KNOWN },
                learningWords = words.count { it.status == WordStatus.LEARNING },
                knownWordsByDay = knownWordsByDay(words, today - ProgressUiState.CHART_DAYS + 1..today, clock),
                activeDays = (today - ProgressUiState.CALENDAR_DAYS + 1..today).map { it in active },
                streak = streak(active, today),
                levelComprehension = levelComprehension(texts, words.toStatusMap()),
                global = globalProgress(summaries, today),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProgressUiState.Loading,
    )

    /** Tapping a language of the global card shows its details, as the language switcher would. */
    fun onLanguageSelected(languageCode: LanguageCode) {
        viewModelScope.launch { preferencesRepository.setLearningLanguage(languageCode) }
    }
}
