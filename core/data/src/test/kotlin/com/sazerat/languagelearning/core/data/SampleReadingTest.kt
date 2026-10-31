package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.sample.SampleEnglishReading
import com.sazerat.languagelearning.core.data.sample.SampleFrenchReading
import com.sazerat.languagelearning.core.data.sample.SampleRomanianReading
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Checks on the sample texts (spec §5.1, §6.2). Building them already fails on an unknown word. */
class SampleReadingTest {
    private val uiLanguages = setOf("en", "fr", "ro")
    private val texts = SampleRomanianReading.texts + SampleEnglishReading.texts + SampleFrenchReading.texts
    private val lemmas = SampleRomanianReading.lemmas + SampleEnglishReading.lemmas + SampleFrenchReading.lemmas

    // Translated into exactly the UI languages other than the text's own language.
    private fun assertTranslatedFrom(language: String, text: LocalizedText, what: String) {
        assertEquals(uiLanguages - language, text.translations.keys, what)
    }

    @Test
    fun `every language has texts and every text has words`() {
        assertEquals(setOf("ro-RO", "en-GB", "fr-FR"), texts.map { it.languageCode.value }.toSet())
        texts.forEach { assertTrue(it.wordLemmas.isNotEmpty(), "${it.id.value} has no word") }
    }

    @Test
    fun `text and lemma ids are unique`() {
        assertEquals(texts.size, texts.map { it.id }.toSet().size)
        assertEquals(lemmas.size, lemmas.map { it.id }.toSet().size)
    }

    @Test
    fun `every word of a text points to a lemma of its language`() {
        val lemmaLanguages = lemmas.associate { it.id to it.languageCode }
        texts.forEach { text ->
            text.wordLemmas.forEach { lemmaId ->
                assertEquals(text.languageCode, lemmaLanguages[lemmaId], "${text.id.value}: ${lemmaId.value}")
            }
        }
    }

    @Test
    fun `every lemma is used by at least one text`() {
        val used = texts.flatMap { it.wordLemmas }.toSet()
        lemmas.forEach { assertTrue(it.id in used, "${it.id.value} is never used") }
    }

    @Test
    fun `sentences and meanings are translated into the other UI languages`() {
        texts.forEach { text ->
            text.sentences.forEach { assertTranslatedFrom(text.languageCode.language, it.translation, text.id.value) }
        }
        lemmas.forEach { lemma ->
            assertTranslatedFrom(lemma.languageCode.language, lemma.meaning, lemma.id.value)
            lemma.otherMeanings?.let { assertTranslatedFrom(lemma.languageCode.language, it, "${lemma.id.value} other") }
        }
    }

    @Test
    fun `notes are written in every UI language`() {
        val notes = lemmas.mapNotNull { it.pronunciationNote } +
            texts.flatMap { text -> text.sentences.flatMap { s -> s.tokens.mapNotNull { it.formNote } } }
        notes.forEach { assertEquals(uiLanguages, it.translations.keys, it.toString()) }
    }

    @Test
    fun `frequency ranks are unique within a language`() {
        lemmas.groupBy { it.languageCode }.forEach { (language, group) ->
            assertEquals(group.size, group.map { it.frequencyRank }.toSet().size, language.value)
        }
    }

    @Test
    fun `Romanian content uses comma-below letters, never cedillas`() {
        val cedillas = setOf('ş', 'Ş', 'ţ', 'Ţ')
        val romanian = SampleRomanianReading.texts.flatMap { text -> listOf(text.title) + text.sentences.map { it.text } } +
            SampleRomanianReading.lemmas.map { it.text } +
            lemmas.flatMap { it.meaning.translations.filterKeys { key -> key == "ro" }.values }
        romanian.forEach { assertFalse(it.any { char -> char in cedillas }, "Cedilla in: $it") }
    }
}
