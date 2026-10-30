package com.sazerat.languagelearning.feature.grammar.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import com.sazerat.languagelearning.feature.grammar.navigation.LessonList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface LessonListUiState {
    data object Loading : LessonListUiState

    data class Success(val lessons: List<Lesson>) : LessonListUiState
}

@HiltViewModel
class LessonListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repository: LessonRepository,
) : ViewModel() {
    val languageCode = LanguageCode(checkNotNull(savedStateHandle[LessonList.LANGUAGE_CODE_KEY]))

    val uiState: StateFlow<LessonListUiState> = repository.observeLessons(languageCode)
        .map(LessonListUiState::Success)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LessonListUiState.Loading,
        )
}
