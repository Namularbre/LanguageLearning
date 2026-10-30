package com.sazerat.languagelearning.feature.pronunciation.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.repository.PronunciationRepository
import com.sazerat.languagelearning.feature.pronunciation.navigation.PronunciationList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface PronunciationListUiState {
    data object Loading : PronunciationListUiState

    data class Success(val exercises: List<PronunciationExercise>) : PronunciationListUiState
}

@HiltViewModel
class PronunciationListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: PronunciationRepository,
) : ViewModel() {
    val languageCode = LanguageCode(checkNotNull(savedStateHandle[PronunciationList.LANGUAGE_CODE_KEY]))

    val uiState: StateFlow<PronunciationListUiState> = repository.observeExercises(languageCode)
        .map(PronunciationListUiState::Success)
        .stateIn(
            scope = viewModelScope,
            // Keeps the upstream alive 5 s after the UI stops collecting, e.g. during a rotation.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PronunciationListUiState.Loading,
        )
}
