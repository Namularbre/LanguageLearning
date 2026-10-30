package com.sazerat.languagelearning.core.domain.model

public data class Lesson(
    val id: LessonId,
    val languageCode: LanguageCode,
    /** Position in the curriculum, starting at 1. */
    val order: Int,
    val title: LocalizedText,
    val summary: LocalizedText,
    val sections: List<LessonSection>,
)

public data class LessonSection(
    val heading: LocalizedText,
    val body: LocalizedText,
    /** Words or phrases in the lesson's language that the learner can listen to. */
    val examples: List<LessonExample> = emptyList(),
)

public data class LessonExample(
    /** Text in the lesson's language, spoken aloud. */
    val text: String,
    /** Meaning in the UI languages other than the lesson's own language. */
    val translation: LocalizedText,
)
