package com.sazerat.languagelearning.core.domain.repository

import com.sazerat.languagelearning.core.domain.model.DailyGoal
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

public interface UserPreferencesRepository {
    public val preferences: Flow<UserPreferences>

    public suspend fun setLearningLanguage(languageCode: LanguageCode)

    public suspend fun setDailyGoal(goal: DailyGoal)

    public suspend fun setPronunciationHelp(enabled: Boolean)

    public suspend fun setOnboardingDone()
}
