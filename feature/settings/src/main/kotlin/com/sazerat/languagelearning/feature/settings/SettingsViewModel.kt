package com.sazerat.languagelearning.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsUiState {
    data object Loading : SettingsUiState

    data class Success(
        val learningLanguage: LanguageCode,
        val dailyGoal: DailyGoal,
        val pronunciationHelp: Boolean,
    ) : SettingsUiState
}

/**
 * The learner's choices kept in DataStore. The UI language is not here: it is the system's
 * per-app language, set directly by the screen (see AppLanguage in :core:ui).
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: UserPreferencesRepository) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> = repository.preferences.map(UserPreferences::toUiState).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState.Loading,
    )

    fun onLearningLanguageSelected(languageCode: LanguageCode) {
        viewModelScope.launch { repository.setLearningLanguage(languageCode) }
    }

    fun onDailyGoalSelected(goal: DailyGoal) {
        viewModelScope.launch { repository.setDailyGoal(goal) }
    }

    fun onPronunciationHelpChange(enabled: Boolean) {
        viewModelScope.launch { repository.setPronunciationHelp(enabled) }
    }
}

private fun UserPreferences.toUiState() = SettingsUiState.Success(learningLanguageOrDefault, dailyGoal, pronunciationHelp)
