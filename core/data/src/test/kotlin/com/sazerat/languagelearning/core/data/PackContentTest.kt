package com.sazerat.languagelearning.core.data

import com.sazerat.languagelearning.core.data.pack.ContentPack
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Checks on the packs shipped in the APK (spec §5.1, §6.2). A pack produced by the content pipeline
 * must pass them before it is published: run `./gradlew :core:data:testDebugUnitTest`.
 */
class PackContentTest {
    private val packs = TestPacks.all
    private val uiLanguages = setOf("en", "fr", "ro")
    private val cedillas = setOf('ş', 'Ş', 'ţ', 'Ţ')

    private val texts = packs.flatMap(ContentPack::toTexts)
    private val lemmas = packs.flatMap(ContentPack::toLemmas)
    private val lessons = packs.flatMap(ContentPack::toLessons)

    // Translated into exactly the UI languages other than the content's own language.
    private fun assertTranslatedFrom(language: String, text: LocalizedText, what: String) {
        assertEquals(uiLanguages - language, text.translations.keys, what)
    }

    /** Either written in every UI language, or a single text shared by all (e.g. "ship / sheep"). */
    private fun assertTranslated(text: LocalizedText, what: String) {
        val languages = text.translations.keys
        assertTrue(languages == uiLanguages || languages == setOf("en"), "$what is only in $languages")
    }

    @Test
    fun `there is one pack per language, named after it`() {
        assertEquals(listOf("en-GB", "fr-FR", "ro-RO"), packs.map { it.languageCode })
        val files = TestPacks.directory.listFiles().orEmpty().map { it.name }.sorted()
        assertEquals(packs.map { "${it.languageCode}.json" }, files)
    }

    @Test
    fun `every pack has texts and every text has words`() {
        packs.forEach { assertTrue(it.texts.isNotEmpty(), "${it.languageCode} has no text") }
        texts.forEach { assertTrue(it.wordLemmas.isNotEmpty(), "${it.id.value} has no word") }
    }

    @Test
    fun `ids are unique`() {
        assertEquals(texts.size, texts.map { it.id }.toSet().size, "text ids")
        assertEquals(lemmas.size, lemmas.map { it.id }.toSet().size, "lemma ids")
        assertEquals(lessons.size, lessons.map { it.id }.toSet().size, "lesson ids")
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
    fun `lessons of each language are numbered 1 to 6`() {
        lessons.groupBy { it.languageCode }.forEach { (language, group) ->
            assertEquals((1..6).toList(), group.map { it.order }.sorted(), language.value)
        }
    }

    @Test
    fun `lesson texts exist in every UI language`() {
        lessons.forEach { lesson ->
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
        lessons.forEach { lesson ->
            lesson.sections.flatMap { it.examples }.forEach { example ->
                assertTranslatedFrom(lesson.languageCode.language, example.translation, "Translations of \"${example.text}\"")
            }
        }
    }

    @Test
    fun `examples are unique within a section`() {
        // The lesson screen identifies the example being played by its text.
        lessons.flatMap { it.sections }.forEach { section ->
            val examples = section.examples.map { it.text }
            assertEquals(examples.size, examples.toSet().size, "Duplicate example in \"${section.heading}\"")
        }
    }

    @Test
    fun `Romanian content uses comma-below letters, never cedillas`() {
        val romanianTexts = texts.filter { it.languageCode.language == "ro" }
            .flatMap { text -> listOf(text.title) + text.sentences.map { it.text } }
        val romanianLemmas = lemmas.filter { it.languageCode.language == "ro" }.map { it.text }
        val romanianExamples = lessons.filter { it.languageCode.language == "ro" }
            .flatMap { it.sections }.flatMap { it.examples }.map { it.text }
        val romanianTranslations = (
            lemmas.map { it.meaning } +
                texts.flatMap { it.sentences }.map { it.translation } +
                lessons.flatMap { lesson ->
                    listOf(lesson.title, lesson.summary) +
                        lesson.sections.flatMap { listOf(it.heading, it.body) + it.examples.map { e -> e.translation } }
                }
            ).mapNotNull { it.translations["ro"] }
        (romanianTexts + romanianLemmas + romanianExamples + romanianTranslations).forEach { text ->
            assertFalse(text.any { it in cedillas }, "Cedilla in: $text")
        }
    }
}
