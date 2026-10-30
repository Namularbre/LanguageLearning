package com.sazerat.languagelearning.feature.grammar

import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.Lesson
import com.sazerat.languagelearning.core.domain.model.LessonExample
import com.sazerat.languagelearning.core.domain.model.LessonId
import com.sazerat.languagelearning.core.domain.model.LessonSection
import com.sazerat.languagelearning.core.domain.model.LocalizedText

/** Data for @Preview and tests only. */
internal val previewLessons = listOf(
    Lesson(
        id = LessonId("preview-genres"),
        languageCode = LanguageCode("ro-RO"),
        order = 1,
        title = LocalizedText.of(en = "Noun gender"),
        summary = LocalizedText.of(en = "Masculine, feminine… and neuter."),
        sections = listOf(
            LessonSection(
                heading = LocalizedText.of(en = "Three genders"),
                body = LocalizedText.of(en = "Masculine, feminine and neuter."),
                examples = listOf(
                    LessonExample("un băiat", LocalizedText.of(en = "a boy")),
                    LessonExample("o fată", LocalizedText.of(en = "a girl")),
                ),
            ),
            LessonSection(LocalizedText.of(en = "The neuter"), LocalizedText.of(en = "un tren → două trenuri.")),
        ),
    ),
    Lesson(
        id = LessonId("preview-pluriel"),
        languageCode = LanguageCode("ro-RO"),
        order = 2,
        title = LocalizedText.of(en = "Plural of nouns"),
        summary = LocalizedText.of(en = "Plural endings and sound changes."),
        sections = listOf(LessonSection(LocalizedText.of(en = "Feminine"), LocalizedText.of(en = "casă → case."))),
    ),
)
