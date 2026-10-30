package com.sazerat.languagelearning.speech.api

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class SpeechPlayerTest {
    private val language = LanguageCode("ro-RO")

    /** Each call waits for the test to finish it, so overlapping playback can be observed. */
    private class GatedTextToSpeech : TextToSpeech {
        val calls = mutableListOf<String>()
        val cancelled = mutableListOf<String>()
        var gate = CompletableDeferred<SpeakResult>()

        override suspend fun speak(text: String, languageCode: LanguageCode, rate: SpeechRate): SpeakResult {
            calls += text
            try {
                return gate.await()
            } catch (e: CancellationException) {
                cancelled += text
                throw e
            }
        }
    }

    @Test
    fun `playing text is exposed until speech ends`() = runTest(UnconfinedTestDispatcher()) {
        val tts = GatedTextToSpeech()
        val player = SpeechPlayer(tts, backgroundScope)

        player.play("casă", language, SpeechRate.NORMAL)
        assertEquals("casă", player.playingText.value)

        tts.gate.complete(SpeakResult.Done)
        assertNull(player.playingText.value)
    }

    @Test
    fun `a new text stops the previous one`() = runTest(UnconfinedTestDispatcher()) {
        val tts = GatedTextToSpeech()
        val player = SpeechPlayer(tts, backgroundScope)

        player.play("casă", language, SpeechRate.NORMAL)
        player.play("țară", language, SpeechRate.NORMAL)

        assertEquals(listOf("casă"), tts.cancelled)
        assertEquals(listOf("casă", "țară"), tts.calls)
        assertEquals("țară", player.playingText.value)
    }

    @Test
    fun `a failure is kept until cleared`() = runTest(UnconfinedTestDispatcher()) {
        val tts = GatedTextToSpeech()
        val player = SpeechPlayer(tts, backgroundScope)

        player.play("casă", language, SpeechRate.NORMAL)
        tts.gate.complete(SpeakResult.Failed(SpeechError.UNSUPPORTED_LANGUAGE))
        assertEquals(SpeechError.UNSUPPORTED_LANGUAGE, player.error.value)

        player.clearError()
        assertNull(player.error.value)
    }
}
