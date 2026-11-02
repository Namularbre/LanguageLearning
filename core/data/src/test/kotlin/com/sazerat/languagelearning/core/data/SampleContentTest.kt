package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.sample.SamplePronunciationExercises
import com.sazerat.languagelearning.core.data.sample.SampleRomanianPronunciation
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Checks on the sample pronunciation exercises. Texts, lexicon and lessons: see [PackContentTest]. */
class SampleContentTest {
    private val exercises = SamplePronunciationExercises.all
    private val uiLanguages = setOf("en", "fr", "ro")
    private val cedillas = setOf('ş', 'Ş', 'ţ', 'Ţ')

    /** Either written in every UI language, or a single text shared by all (e.g. "ship / sheep"). */
    private fun assertTranslated(text: LocalizedText, what: String) {
        val languages = text.translations.keys
        assertTrue(languages == uiLanguages || languages == setOf("en"), "$what is only in $languages")
    }

    @Test
    fun `exercise ids are unique`() {
        val ids = exercises.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `every IPA transcription is between slashes`() {
        exercises.flatMap { it.items }.forEach { item ->
            assertTrue(item.ipa.startsWith("/") && item.ipa.endsWith("/"), "Bad IPA for ${item.text}: ${item.ipa}")
        }
    }

    @Test
    fun `exercise texts exist in every UI language`() {
        exercises.forEach { exercise ->
            val id = exercise.id.value
            assertTranslated(exercise.title, "$id title")
            assertTranslated(exercise.focus, "$id focus")
            assertEquals(uiLanguages, exercise.instructions.translations.keys, "$id instructions")
        }
    }

    @Test
    fun `romanian text never uses cedilla s or t`() {
        val romanianTexts = SampleRomanianPronunciation.all.flatMap { it.items }.map { it.text } +
            exercises.flatMap { listOf(it.title, it.focus, it.instructions) }.mapNotNull { it.translations["ro"] }
        romanianTexts.forEach { text ->
            assertFalse(text.any { it in cedillas }, "Cedilla found in \"$text\"")
        }
    }

    @Test
    fun `items are unique within an exercise`() {
        // The pronunciation screen identifies the item being played by its text.
        exercises.forEach { exercise ->
            val texts = exercise.items.map { it.text }
            assertEquals(texts.size, texts.toSet().size, "Duplicate item in \"${exercise.id.value}\"")
        }
    }
}
