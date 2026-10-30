package com.sazerat.languagelearning.feature.pronunciation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.ExerciseId
import com.sazerat.languagelearning.core.domain.model.PronunciationExercise
import com.sazerat.languagelearning.core.domain.model.PronunciationItem
import com.sazerat.languagelearning.core.domain.repository.PronunciationRepository
import com.sazerat.languagelearning.feature.pronunciation.navigation.PronunciationDetail
import com.sazerat.languagelearning.speech.api.SpeechError
import com.sazerat.languagelearning.speech.api.SpeechPlayer
import com.sazerat.languagelearning.speech.api.SpeechRate
import com.sazerat.languagelearning.speech.api.TextToSpeech
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface PronunciationDetailUiState {
    data object Loading : PronunciationDetailUiState

    data object NotFound : PronunciationDetailUiState

    data class Success(
        val exercise: PronunciationExercise,
        /** Text of the item being spoken right now, if any. */
        val playingText: String? = null,
        val slowSpeed: Boolean = false,
        /** Shown once in a snackbar, then cleared with [PronunciationDetailViewModel.onSpeechErrorShown]. */
        val speechError: SpeechError? = null,
    ) : PronunciationDetailUiState
}

@HiltViewModel
class PronunciationDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    repository: PronunciationRepository,
    textToSpeech: TextToSpeech,
) : ViewModel() {
    // Navigation stores the route arguments in the SavedStateHandle, so they survive process death.
    // Read by key rather than with toRoute(): toRoute() needs a real android.os.Bundle,
    // which would force Robolectric into the JVM unit tests.
    private val exerciseId = ExerciseId(checkNotNull(savedStateHandle[PronunciationDetail.EXERCISE_ID_KEY]))

    private val player = SpeechPlayer(textToSpeech, viewModelScope)
    private val slowSpeed = savedStateHandle.getStateFlow(SLOW_SPEED_KEY, false)

    val uiState: StateFlow<PronunciationDetailUiState> = combine(
        repository.observeExercise(exerciseId),
        player.playingText,
        slowSpeed,
        player.error,
    ) { exercise, playing, slow, error ->
        if (exercise == null) {
            PronunciationDetailUiState.NotFound
        } else {
            PronunciationDetailUiState.Success(exercise, playing, slow, error)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PronunciationDetailUiState.Loading,
    )

    fun onItemClick(item: PronunciationItem) {
        val exercise = (uiState.value as? PronunciationDetailUiState.Success)?.exercise ?: return
        player.play(item.text, exercise.languageCode, if (slowSpeed.value) SpeechRate.SLOW else SpeechRate.NORMAL)
    }

    fun onSlowSpeedChange(slow: Boolean) {
        savedStateHandle[SLOW_SPEED_KEY] = slow
    }

    fun onSpeechErrorShown() {
        player.clearError()
    }

    private companion object {
        const val SLOW_SPEED_KEY = "slowSpeed"
    }
}
