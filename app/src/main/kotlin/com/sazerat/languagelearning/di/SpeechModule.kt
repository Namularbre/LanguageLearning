package com.sazerat.languagelearning.di

import android.content.Context
import com.sazerat.languagelearning.speech.android.AndroidTextToSpeech
import com.sazerat.languagelearning.speech.api.TextToSpeech
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Speech capabilities. Today: the on-device system engine. Azure voices can be swapped in here later. */
@Module
@InstallIn(SingletonComponent::class)
object SpeechModule {
    // Singleton: starting the system TTS engine is slow, so it is created once for the whole app.
    @Provides
    @Singleton
    fun provideTextToSpeech(@ApplicationContext context: Context): TextToSpeech = AndroidTextToSpeech(context)
}
