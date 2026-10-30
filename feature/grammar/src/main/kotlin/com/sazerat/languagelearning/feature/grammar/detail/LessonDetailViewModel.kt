package com.sazerat.languagelearning.feature.grammar.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonExample
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import com.sazerat.languagelearning.feature.grammar.navigation.LessonDetail
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

sealed interface LessonDetailUiState {
    data object Loading : LessonDetailUiState

    data object NotFound : LessonDetailUiState

    data class Success(
        val lesson: Lesson,
        /** Text of the example being spoken right now, if any. */
        val playingText: String? = null,
        val slowSpeed: Boolean = false,
        /** Shown once in a snackbar, then cleared with [LessonDetailViewModel.onSpeechErrorShown]. */
        val speechError: SpeechError? = null,
    ) : LessonDetailUiState
}

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    repository: LessonRepository,
    textToSpeech: TextToSpeech,
) : ViewModel() {
    private val lessonId = LessonId(checkNotNull(savedStateHandle[LessonDetail.LESSON_ID_KEY]))

    private val player = SpeechPlayer(textToSpeech, viewModelScope)

    // A user preference for this screen: saved so it survives process death.
    private val slowSpeed = savedStateHandle.getStateFlow(SLOW_SPEED_KEY, false)

    val uiState: StateFlow<LessonDetailUiState> = combine(
        repository.observeLesson(lessonId),
        player.playingText,
        slowSpeed,
        player.error,
    ) { lesson, playing, slow, error ->
        if (lesson == null) {
            LessonDetailUiState.NotFound
        } else {
            LessonDetailUiState.Success(lesson, playing, slow, error)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LessonDetailUiState.Loading,
    )

    fun onExampleClick(example: LessonExample) {
        val lesson = (uiState.value as? LessonDetailUiState.Success)?.lesson ?: return
        player.play(example.text, lesson.languageCode, if (slowSpeed.value) SpeechRate.SLOW else SpeechRate.NORMAL)
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
