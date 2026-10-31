package com.sazerat.languagelearning.core.domain.model

/** Minutes of practice a day the learner aims for (spec §4). */
public enum class DailyGoal(public val minutes: Int) {
    FIVE(minutes = 5),
    TEN(minutes = 10),
    TWENTY(minutes = 20),
    THIRTY(minutes = 30),
    ;

    public companion object {
        public val Default: DailyGoal = TEN
    }
}

/**
 * Choices kept on the device. The UI language (and native language) is not here: it is the
 * system's per-app language, so Android and the string resources follow it.
 */
public data class UserPreferences(
    /** `null` until chosen during onboarding. */
    val learningLanguage: LanguageCode? = null,
    val dailyGoal: DailyGoal = DailyGoal.Default,
    /** Shows IPA, pronunciation notes and exercises (spec §7). */
    val pronunciationHelp: Boolean = false,
    val onboardingDone: Boolean = false,
)
