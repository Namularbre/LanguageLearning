package com.sazerat.languagelearning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Decides where the app starts: the onboarding on first launch, Today afterwards. */
@HiltViewModel
class MainViewModel @Inject constructor(preferencesRepository: UserPreferencesRepository) : ViewModel() {
    /** `null` while the preferences load. */
    val onboardingDone: StateFlow<Boolean?> = preferencesRepository.preferences
        .map { it.onboardingDone }
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)
}
