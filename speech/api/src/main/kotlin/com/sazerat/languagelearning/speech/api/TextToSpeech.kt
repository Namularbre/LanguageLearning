package com.sazerat.languagelearning.speech.api

import com.sazerat.languagelearning.core.domain.model.LanguageCode

/**
 * Speech synthesis capability (spec §11.3). Features only see this interface: the Android system
 * engine is used today, a cloud voice (Azure) can replace it later without touching `:feature:*`.
 */
public interface TextToSpeech {
    /** Suspends until the text has been spoken. Cancelling the coroutine stops the audio. */
    public suspend fun speak(text: String, languageCode: LanguageCode, rate: SpeechRate = SpeechRate.NORMAL): SpeakResult
}

/** Playback speeds offered to the learner (spec §4: 0.75×, 1× and 1.25× in the Reader). */
public enum class SpeechRate(public val factor: Float) {
    NORMAL(factor = 1.0f),
    SLOW(factor = 0.75f),
    FAST(factor = 1.25f),
}

public sealed interface SpeakResult {
    public data object Done : SpeakResult

    public data class Failed(val error: SpeechError) : SpeakResult
}

public enum class SpeechError {
    /** No voice installed for this language on the device. */
    UNSUPPORTED_LANGUAGE,

    /** The synthesis engine could not start or failed while speaking. */
    ENGINE_ERROR,
}
