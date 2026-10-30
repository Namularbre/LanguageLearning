package com.sazerat.languagelearning.feature.session.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.SessionDuration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    /**
     * Kept in the SavedStateHandle so the choice survives process death.
     * M2 will store the user's preferred duration in DataStore instead.
     */
    val selectedDuration: StateFlow<SessionDuration> =
        savedStateHandle.getStateFlow(SELECTED_DURATION_KEY, SessionDuration.Default)

    /**
     * Language code chosen by the user, or `null` before any choice: the screen then uses the first
     * language the app offers. Stored as a String, which the SavedStateHandle can save.
     */
    val selectedLanguage: StateFlow<String?> = savedStateHandle.getStateFlow(SELECTED_LANGUAGE_KEY, null)

    fun onDurationSelected(duration: SessionDuration) {
        savedStateHandle[SELECTED_DURATION_KEY] = duration
    }

    fun onLanguageSelected(languageCode: LanguageCode) {
        savedStateHandle[SELECTED_LANGUAGE_KEY] = languageCode.value
    }

    private companion object {
        const val SELECTED_DURATION_KEY = "selectedDuration"
        const val SELECTED_LANGUAGE_KEY = "selectedLanguage"
    }
}
