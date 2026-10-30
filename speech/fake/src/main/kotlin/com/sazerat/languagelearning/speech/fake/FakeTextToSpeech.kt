package com.sazerat.languagelearning.speech.fake

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.speech.api.SpeakResult
import com.sazerat.languagelearning.speech.api.SpeechRate
import com.sazerat.languagelearning.speech.api.TextToSpeech
import kotlinx.coroutines.CompletableDeferred

/** Records what was asked to be spoken. Tests choose the result and when speaking finishes. */
class FakeTextToSpeech : TextToSpeech {
    data class Request(val text: String, val languageCode: LanguageCode, val rate: SpeechRate)

    val requests = mutableListOf<Request>()

    /** Result returned by every call to [speak]. */
    var result: SpeakResult = SpeakResult.Done

    /** When set, [speak] suspends until it is completed, so tests can observe the "speaking" state. */
    var gate: CompletableDeferred<Unit>? = null

    override suspend fun speak(text: String, languageCode: LanguageCode, rate: SpeechRate): SpeakResult {
        requests += Request(text, languageCode, rate)
        gate?.await()
        return result
    }
}
