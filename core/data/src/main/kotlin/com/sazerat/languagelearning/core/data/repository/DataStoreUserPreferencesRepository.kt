package com.sazerat.languagelearning.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import com.sazerat.languagelearning.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** One file for the whole app: the delegate makes sure a single DataStore reads and writes it. */
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/** Preferences in DataStore (spec §9.9: never SharedPreferences). */
class DataStoreUserPreferencesRepository(private val dataStore: DataStore<Preferences>) : UserPreferencesRepository {
    override val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            learningLanguage = prefs[LEARNING_LANGUAGE]?.let(::LanguageCode),
            // An unknown name (a goal removed in a later version) falls back to the default.
            dailyGoal = DailyGoal.entries.find { it.name == prefs[DAILY_GOAL] } ?: DailyGoal.Default,
            pronunciationHelp = prefs[PRONUNCIATION_HELP] ?: false,
            onboardingDone = prefs[ONBOARDING_DONE] ?: false,
        )
    }

    override suspend fun setLearningLanguage(languageCode: LanguageCode) {
        dataStore.edit { it[LEARNING_LANGUAGE] = languageCode.value }
    }

    override suspend fun setDailyGoal(goal: DailyGoal) {
        dataStore.edit { it[DAILY_GOAL] = goal.name }
    }

    override suspend fun setPronunciationHelp(enabled: Boolean) {
        dataStore.edit { it[PRONUNCIATION_HELP] = enabled }
    }

    override suspend fun setOnboardingDone() {
        dataStore.edit { it[ONBOARDING_DONE] = true }
    }

    private companion object {
        val LEARNING_LANGUAGE = stringPreferencesKey("learningLanguage")
        val DAILY_GOAL = stringPreferencesKey("dailyGoal")
        val PRONUNCIATION_HELP = booleanPreferencesKey("pronunciationHelp")
        val ONBOARDING_DONE = booleanPreferencesKey("onboardingDone")
    }
}
