package com.sazerat.languagelearning.di

import android.content.Context
import com.sazerat.languagelearning.core.data.SystemClock
import com.sazerat.languagelearning.core.data.database.LanguageLearningDatabase
import com.sazerat.languagelearning.core.data.repository.DataStoreUserPreferencesRepository
import com.sazerat.languagelearning.core.data.repository.InMemoryLessonRepository
import com.sazerat.languagelearning.core.data.repository.InMemoryLexiconRepository
import com.sazerat.languagelearning.core.data.repository.InMemoryPronunciationRepository
import com.sazerat.languagelearning.core.data.repository.InMemoryTextRepository
import com.sazerat.languagelearning.core.data.repository.RoomReadingLogRepository
import com.sazerat.languagelearning.core.data.repository.RoomReviewRepository
import com.sazerat.languagelearning.core.data.repository.RoomWordStatusRepository
import com.sazerat.languagelearning.core.data.repository.userPreferencesDataStore
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.repository.LessonRepository
import com.sazerat.languagelearning.core.domain.repository.LexiconRepository
import com.sazerat.languagelearning.core.domain.repository.PronunciationRepository
import com.sazerat.languagelearning.core.domain.repository.ReadingLogRepository
import com.sazerat.languagelearning.core.domain.repository.ReviewRepository
import com.sazerat.languagelearning.core.domain.repository.TextRepository
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import com.sazerat.languagelearning.core.domain.repository.WordStatusRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Chooses which implementation backs each repository interface. Only `:app` does this (spec §11.1).
 * Content is still in memory (sample data shipped in the APK); the learner's progress is in Room and
 * the preferences in DataStore.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideClock(): Clock = SystemClock()

    // Singleton: one connection pool for the whole app, and Room's Flows only see writes made
    // through the same instance.
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LanguageLearningDatabase = LanguageLearningDatabase.create(context)

    @Provides
    @Singleton
    fun providePronunciationRepository(): PronunciationRepository = InMemoryPronunciationRepository()

    @Provides
    @Singleton
    fun provideLessonRepository(): LessonRepository = InMemoryLessonRepository()

    @Provides
    @Singleton
    fun provideTextRepository(): TextRepository = InMemoryTextRepository()

    @Provides
    @Singleton
    fun provideLexiconRepository(): LexiconRepository = InMemoryLexiconRepository()

    @Provides
    @Singleton
    fun provideWordStatusRepository(database: LanguageLearningDatabase, clock: Clock): WordStatusRepository =
        RoomWordStatusRepository(database.wordStatusDao(), clock)

    @Provides
    @Singleton
    fun provideReviewRepository(database: LanguageLearningDatabase): ReviewRepository = RoomReviewRepository(database.reviewDao())

    @Provides
    @Singleton
    fun provideReadingLogRepository(database: LanguageLearningDatabase): ReadingLogRepository =
        RoomReadingLogRepository(database.readingLogDao())

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository =
        DataStoreUserPreferencesRepository(context.userPreferencesDataStore)
}
