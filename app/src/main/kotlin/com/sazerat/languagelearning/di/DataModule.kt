package com.sazerat.languagelearning.di

import com.sazerat.languagelearning.core.data.repository.InMemoryLessonRepository
import com.sazerat.languagelearning.core.data.repository.InMemoryPronunciationRepository
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import com.sazerat.languagelearning.core.domain.repository.PronunciationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Chooses which implementation backs each repository interface. Only `:app` does this (spec §11.1):
 * swapping the M1 in-memory fakes for Room in M2 only changes this file.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun providePronunciationRepository(): PronunciationRepository = InMemoryPronunciationRepository()

    @Provides
    @Singleton
    fun provideLessonRepository(): LessonRepository = InMemoryLessonRepository()
}
