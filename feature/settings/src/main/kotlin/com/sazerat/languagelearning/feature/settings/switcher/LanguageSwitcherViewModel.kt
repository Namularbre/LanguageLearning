package com.sazerat.languagelearning.feature.settings.switcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.progress.LanguageSummary
import com.sazerat.languagelearning.core.domain.progress.StudiedLanguages
import com.sazerat.languagelearning.core.domain.progress.observeLanguageSummaries
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.ReviewRepository
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LanguageSwitcherUiState {
    data object Loading : LanguageSwitcherUiState

    data class Success(
        val current: LanguageCode,
        /** Every studied language, in display order, with where the learner stands in it. */
        val languages: List<LanguageSummary>,
    ) : LanguageSwitcherUiState
}

/**
 * The language being learnt, switchable from the top of every tab: a learner may study several
 * languages and should not have to dig into the settings to go from one to the other.
 */
@HiltViewModel
class LanguageSwitcherViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    wordStatusRepository: WordStatusRepository,
    reviewRepository: ReviewRepository,
    readingLogRepository: ReadingLogRepository,
    clock: Clock,
) : ViewModel() {
    val uiState: StateFlow<LanguageSwitcherUiState> = combine(
        preferencesRepository.preferences,
        observeLanguageSummaries(StudiedLanguages, wordStatusRepository, reviewRepository, readingLogRepository, clock),
    ) { preferences, summaries ->
        LanguageSwitcherUiState.Success(preferences.learningLanguageOrDefault, summaries)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LanguageSwitcherUiState.Loading,
    )

    fun onLanguageSelected(languageCode: LanguageCode) {
        viewModelScope.launch { preferencesRepository.setLearningLanguage(languageCode) }
    }
}
