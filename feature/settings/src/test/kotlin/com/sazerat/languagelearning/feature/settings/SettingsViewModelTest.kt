package com.sazerat.languagelearning.feature.settings

import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.testing.MainDispatcherRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = object : UserPreferencesRepository {
        override val preferences = MutableStateFlow(UserPreferences())

        override suspend fun setLearningLanguage(languageCode: LanguageCode) = preferences.update { it.copy(learningLanguage = languageCode) }

        override suspend fun setDailyGoal(goal: DailyGoal) = preferences.update { it.copy(dailyGoal = goal) }

        override suspend fun setPronunciationHelp(enabled: Boolean) = preferences.update { it.copy(pronunciationHelp = enabled) }

        override suspend fun setOnboardingDone() = preferences.update { it.copy(onboardingDone = true) }
    }

    private val viewModel = SettingsViewModel(repository)

    @Test
    fun `defaults are shown before any choice`() = runTest {
        assertEquals(
            SettingsUiState.Success(UserPreferences.DEFAULT_LEARNING_LANGUAGE, DailyGoal.TEN, pronunciationHelp = false),
            viewModel.successState(),
        )
    }

    @Test
    fun `choices are saved`() = runTest {
        viewModel.successState()

        viewModel.onLearningLanguageSelected(LanguageCode("fr-FR"))
        viewModel.onDailyGoalSelected(DailyGoal.TWENTY)
        viewModel.onPronunciationHelpChange(true)

        // Each choice is saved in its own coroutine: wait for the last one.
        val state = viewModel.successState { it.pronunciationHelp }
        assertEquals(SettingsUiState.Success(LanguageCode("fr-FR"), DailyGoal.TWENTY, pronunciationHelp = true), state)
    }

    private suspend fun SettingsViewModel.successState(until: (SettingsUiState.Success) -> Boolean = { true }) =
        uiState.first { it is SettingsUiState.Success && until(it) } as SettingsUiState.Success
}
