package com.sazerat.languagelearning.core.domain.reading

import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.domain.model.Token
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ComprehensionTest {
    // A text whose words are the given lemma ids, one occurrence each.
    private fun text(id: String, vararg lemmas: String, level: CefrLevel = CefrLevel.A1, minutes: Int = 2) = ReadingText(
        id = TextId(id),
        languageCode = LanguageCode("ro-RO"),
        level = level,
        kind = TextKind.STORY,
        title = id,
        minutes = minutes,
        sentences = listOf(
            Sentence(
                tokens = lemmas.flatMap { listOf(Token(it, LemmaId(it)), Token(" ")) },
                translation = LocalizedText.of(en = "-"),
            ),
        ),
    )

    private fun statuses(vararg pairs: Pair<String, WordStatus>) = pairs.associate { LemmaId(it.first) to it.second }

    @Test
    fun `known and learning words count as understood, new ones do not`() {
        val text = text("t", "a", "b", "c", "d")

        val comprehension = text.comprehension(statuses("a" to WordStatus.KNOWN, "b" to WordStatus.LEARNING, "c" to WordStatus.NEW))

        assertEquals(50, comprehension)
    }

    @Test
    fun `each occurrence counts`() {
        val text = text("t", "a", "a", "a", "b")

        assertEquals(75, text.comprehension(statuses("a" to WordStatus.KNOWN)))
    }

    @Test
    fun `suggestion is the easiest unread text that still teaches something`() {
        val texts = listOf(text("easy", "a", "b"), text("medium", "a", "b", "c", "d"), text("hard", "c", "d"))
        val known = statuses("a" to WordStatus.KNOWN, "b" to WordStatus.KNOWN)

        // "easy" is fully known (100 %), so "medium" (50 %) comes before "hard" (0 %).
        assertEquals(TextId("medium"), suggestNextText(texts, known, readTexts = emptySet())?.id)
    }

    @Test
    fun `read texts are never suggested`() {
        val texts = listOf(text("one", "a"), text("two", "b"))

        assertEquals(TextId("two"), suggestNextText(texts, emptyMap(), readTexts = setOf(TextId("one")))?.id)
    }

    @Test
    fun `when every unread text is known, the least known is suggested`() {
        val texts = listOf(text("one", "a", "b"), text("two", "a"))
        val known = statuses("a" to WordStatus.KNOWN, "b" to WordStatus.KNOWN)

        assertEquals(TextId("one"), suggestNextText(texts, known, readTexts = setOf(TextId("two")))?.id)
    }

    @Test
    fun `ties go to the lower level, then the shorter text`() {
        val texts = listOf(
            text("b1", "x", level = CefrLevel.B1),
            text("a1-long", "x", minutes = 5),
            text("a1-short", "x", minutes = 2),
        )

        assertEquals(TextId("a1-short"), suggestNextText(texts, emptyMap(), emptySet())?.id)
    }

    @Test
    fun `nothing is suggested once everything is read`() {
        assertNull(suggestNextText(listOf(text("one", "a")), emptyMap(), setOf(TextId("one"))))
    }

    @Test
    fun `nothing is suggested for rereading while a text is unread`() {
        val log = listOf(ReadingLogEntry(TextId("a"), minutes = 2, finishedAt = 10))

        assertNull(suggestReread(listOf(text("a", "x"), text("b", "y")), log))
        assertNull(suggestReread(listOf(text("a", "x")), emptyList()))
    }

    @Test
    fun `once everything is read, the text read longest ago is suggested again`() {
        val log = listOf(
            ReadingLogEntry(TextId("a"), minutes = 2, finishedAt = 10),
            ReadingLogEntry(TextId("b"), minutes = 2, finishedAt = 20),
            // Read again since: now the most recent.
            ReadingLogEntry(TextId("a"), minutes = 2, finishedAt = 30),
        )

        assertEquals(TextId("b"), suggestReread(listOf(text("a", "x"), text("b", "y")), log)?.id)
    }
}
