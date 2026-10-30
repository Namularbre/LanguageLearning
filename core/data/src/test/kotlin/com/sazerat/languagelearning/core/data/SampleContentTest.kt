package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.sample.SampleEnglishLessons
import com.sazerat.languagelearning.core.data.sample.SampleFrenchLessons
import com.sazerat.languagelearning.core.data.sample.SamplePronunciationExercises
import com.sazerat.languagelearning.core.data.sample.SampleRomanianLessons
import com.sazerat.languagelearning.core.data.sample.SampleRomanianPronunciation
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SampleContentTest {
    private val allLessons = SampleEnglishLessons.all + SampleRomanianLessons.all + SampleFrenchLessons.all
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
    fun `lesson ids are unique`() {
        val ids = allLessons.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `every IPA transcription is between slashes`() {
        exercises.flatMap { it.items }.forEach { item ->
            assertTrue(item.ipa.startsWith("/") && item.ipa.endsWith("/"), "Bad IPA for ${item.text}: ${item.ipa}")
        }
    }

    @Test
    fun `lessons of each language are numbered 1 to 3`() {
        assertEquals(listOf(1, 2, 3), SampleEnglishLessons.all.map { it.order })
        assertEquals(listOf(1, 2, 3), SampleRomanianLessons.all.map { it.order })
        assertEquals(listOf(1, 2, 3), SampleFrenchLessons.all.map { it.order })
    }

    @Test
    fun `lesson texts exist in every UI language`() {
        allLessons.forEach { lesson ->
            val id = lesson.id.value
            assertTranslated(lesson.title, "$id title")
            assertTranslated(lesson.summary, "$id summary")
            lesson.sections.forEach { section ->
                assertTranslated(section.heading, "$id heading")
                assertTranslated(section.body, "$id body")
            }
        }
    }

    @Test
    fun `examples are translated into every UI language except their own`() {
        allLessons.forEach { lesson ->
            val expected = uiLanguages - lesson.languageCode.language
            lesson.sections.flatMap { it.examples }.forEach { example ->
                assertEquals(expected, example.translation.translations.keys, "Translations of \"${example.text}\"")
            }
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
        val romanianTexts = SampleRomanianLessons.all.flatMap { it.sections }.flatMap { it.examples }.map { it.text } +
            SampleRomanianPronunciation.all.flatMap { it.items }.map { it.text } +
            allLessons.flatMap { lesson ->
                listOf(lesson.title, lesson.summary) +
                    lesson.sections.flatMap { listOf(it.heading, it.body) + it.examples.map { e -> e.translation } }
            }.mapNotNull { it.translations["ro"] } +
            exercises.flatMap { listOf(it.title, it.focus, it.instructions) }.mapNotNull { it.translations["ro"] }
        romanianTexts.forEach { text ->
            assertFalse(text.any { it in cedillas }, "Cedilla found in \"$text\"")
        }
    }

    @Test
    fun `examples are unique within a section`() {
        // The lesson screen identifies the example being played by its text.
        allLessons.flatMap { it.sections }.forEach { section ->
            val texts = section.examples.map { it.text }
            assertEquals(texts.size, texts.toSet().size, "Duplicate example in \"${section.heading}\"")
        }
    }

    @Test
    fun `items are unique within an exercise`() {
        // Same reason: the pronunciation screen identifies the item being played by its text.
        exercises.forEach { exercise ->
            val texts = exercise.items.map { it.text }
            assertEquals(texts.size, texts.toSet().size, "Duplicate item in \"${exercise.id.value}\"")
        }
    }
}
