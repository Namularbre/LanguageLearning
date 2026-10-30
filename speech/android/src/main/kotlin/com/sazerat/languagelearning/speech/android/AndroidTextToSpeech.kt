package com.sazerat.languagelearning.speech.android

import android.content.Context
import android.speech.tts.UtteranceProgressListener
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.speech.api.SpeakResult
import com.sazerat.languagelearning.speech.api.SpeechError
import com.sazerat.languagelearning.speech.api.SpeechRate
import com.sazerat.languagelearning.speech.api.TextToSpeech
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException
import android.speech.tts.TextToSpeech as SystemTextToSpeech

/**
 * [TextToSpeech] backed by the device's speech engine (Google TTS on most phones).
 * Runs on-device and offline once the voice is installed; quality is below cloud neural voices.
 *
 * The engine calls back on its own threads, so each utterance is bridged to a coroutine with a
 * [CompletableDeferred] looked up by utterance id.
 */
class AndroidTextToSpeech(context: Context) : TextToSpeech {
    private val ready = CompletableDeferred<Boolean>()
    private val pending = ConcurrentHashMap<String, CompletableDeferred<SpeakResult>>()

    // Engine calls are synchronous calls into another process: setLanguage() was measured at
    // 25-180 ms, enough to freeze the UI. They run on one background thread, one at a time.
    private val engineDispatcher = Dispatchers.IO.limitedParallelism(1)

    // Language last set successfully; only read and written on engineDispatcher.
    private var currentLanguage: LanguageCode? = null

    private val engine = SystemTextToSpeech(context.applicationContext) { status ->
        ready.complete(status == SystemTextToSpeech.SUCCESS)
    }

    init {
        engine.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String) = Unit

                override fun onDone(utteranceId: String) {
                    pending.remove(utteranceId)?.complete(SpeakResult.Done)
                }

                override fun onStop(utteranceId: String, interrupted: Boolean) {
                    pending.remove(utteranceId)?.complete(SpeakResult.Done)
                }

                override fun onError(utteranceId: String, errorCode: Int) {
                    pending.remove(utteranceId)?.complete(SpeakResult.Failed(SpeechError.ENGINE_ERROR))
                }

                @Deprecated("Replaced by onError(String, Int) in the platform API.")
                override fun onError(utteranceId: String) {
                    onError(utteranceId, SystemTextToSpeech.ERROR)
                }
            },
        )
    }

    override suspend fun speak(text: String, languageCode: LanguageCode, rate: SpeechRate): SpeakResult =
        withContext(engineDispatcher) {
            if (!ready.await()) return@withContext SpeakResult.Failed(SpeechError.ENGINE_ERROR)

            // Changing language can make the engine load another voice: only do it when needed.
            if (languageCode != currentLanguage) {
                when (engine.setLanguage(Locale.forLanguageTag(languageCode.value))) {
                    SystemTextToSpeech.LANG_MISSING_DATA, SystemTextToSpeech.LANG_NOT_SUPPORTED ->
                        return@withContext SpeakResult.Failed(SpeechError.UNSUPPORTED_LANGUAGE)
                }
                currentLanguage = languageCode
            }
            engine.setSpeechRate(rate.factor)

            val utteranceId = UUID.randomUUID().toString()
            val result = CompletableDeferred<SpeakResult>()
            pending[utteranceId] = result
            if (engine.speak(text, SystemTextToSpeech.QUEUE_FLUSH, null, utteranceId) == SystemTextToSpeech.ERROR) {
                pending.remove(utteranceId)
                return@withContext SpeakResult.Failed(SpeechError.ENGINE_ERROR)
            }
            try {
                result.await()
            } catch (e: CancellationException) {
                // The screen went away or another text was tapped: stop talking.
                engine.stop()
                throw e
            }
        }
}
