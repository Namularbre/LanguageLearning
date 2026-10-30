package com.sazerat.languagelearning.speech.api

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * "Listen" buttons behaviour shared by several screens: plays one text at a time and exposes
 * what is playing and the last error. A ViewModel creates one with its `viewModelScope`, so
 * leaving the screen cancels playback.
 */
public class SpeechPlayer(
    private val textToSpeech: TextToSpeech,
    private val scope: CoroutineScope,
) {
    private val _playingText = MutableStateFlow<String?>(null)

    /** Text being spoken right now, or `null`. */
    public val playingText: StateFlow<String?> = _playingText.asStateFlow()

    private val _error = MutableStateFlow<SpeechError?>(null)

    /** Last failure, kept until [clearError] is called (after the UI has shown it). */
    public val error: StateFlow<SpeechError?> = _error.asStateFlow()

    private var job: Job? = null

    public fun play(text: String, languageCode: LanguageCode, rate: SpeechRate) {
        val previous = job
        job = scope.launch {
            // Stop the previous text before starting, so two never overlap.
            previous?.cancelAndJoin()
            _playingText.value = text
            val result = try {
                textToSpeech.speak(text, languageCode, rate)
            } finally {
                _playingText.value = null
            }
            if (result is SpeakResult.Failed) _error.value = result.error
        }
    }

    public fun clearError() {
        _error.value = null
    }
}
