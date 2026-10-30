package com.sazerat.languagelearning.core.domain.model

/** BCP-47 language tag, e.g. "en-GB" or "ro-RO". Every piece of content carries one (spec §9.8). */
@JvmInline
public value class LanguageCode(public val value: String) {
    /** Language part of the tag, e.g. "ro" for "ro-RO": matches the keys of [LocalizedText]. */
    public val language: String get() = value.substringBefore('-')
}

@JvmInline
public value class ExerciseId(public val value: String)

@JvmInline
public value class LessonId(public val value: String)
