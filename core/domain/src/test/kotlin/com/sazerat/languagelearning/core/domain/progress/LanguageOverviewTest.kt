package com.sazerat.languagelearning.core.domain.progress

import com.sazerat.languagelearning.core.domain.model.CardContext
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LearnerWord
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReviewCard
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LanguageOverviewTest {
    private val clock = object : Clock {
        override fun now() = 10 * MILLIS_PER_DAY + 1

        override fun dayOf(millis: Long) = millis / MILLIS_PER_DAY
    }
    private val romanian = LanguageCode("ro-RO")
    private val english = LanguageCode("en-GB")

    private fun at(day: Long) = day * MILLIS_PER_DAY + 1_000
    private fun word(id: String, status: WordStatus) = LearnerWord(LemmaId(id), status, updatedAt = 0)
    private fun card(id: String, due: Long) =
        ReviewCard(LemmaId(id), romanian, CardContext(TextId("t"), 0, 0), memory = null, due = due, createdAt = 0)

    @Test
    fun `a summary counts the words, the due cards and each text read once`() {
        val summary = languageSummary(
            romanian,
            words = listOf(word("a", WordStatus.KNOWN), word("b", WordStatus.KNOWN), word("c", WordStatus.LEARNING)),
            cards = listOf(card("c", due = at(9)), card("d", due = at(12))),
            reviews = listOf(ReviewLogEntry(LemmaId("c"), Rating.GOOD, at(8))),
            readings = listOf(ReadingLogEntry(TextId("t"), 2, at(9)), ReadingLogEntry(TextId("t"), 2, at(10))),
            clock = clock,
        )

        assertEquals(2, summary.knownWords)
        assertEquals(1, summary.learningWords)
        assertEquals(1, summary.dueCards)
        assertEquals(1, summary.textsRead)
        assertEquals(setOf(8L, 9L, 10L), summary.activeDays)
        assertTrue(summary.isStarted)
    }

    @Test
    fun `a language with nothing done is not started`() {
        val summary = languageSummary(english, emptyList(), emptyList(), emptyList(), emptyList(), clock)

        assertFalse(summary.isStarted)
    }

    @Test
    fun `the global streak counts a day practised in any language`() {
        val ro = LanguageSummary(romanian, knownWords = 10, learningWords = 2, dueCards = 0, textsRead = 1, activeDays = setOf(8, 10))
        val en = LanguageSummary(english, knownWords = 5, learningWords = 1, dueCards = 3, textsRead = 0, activeDays = setOf(9))

        val global = globalProgress(listOf(ro, en), today = 10)

        assertEquals(15, global.knownWords)
        assertEquals(3, global.learningWords)
        assertEquals(3, global.streak)
        assertEquals(listOf(ro, en), global.languages)
    }
}
