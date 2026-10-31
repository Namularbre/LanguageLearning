package com.sazerat.languagelearning.core.domain.progress

import com.sazerat.languagelearning.core.domain.model.CefrLevel
import com.sazerat.languagelearning.core.domain.model.Clock
import com.sazerat.languagelearning.core.domain.model.LanguageCode
import com.sazerat.languagelearning.core.domain.model.LearnerWord
import com.sazerat.languagelearning.core.domain.model.LemmaId
import com.sazerat.languagelearning.core.domain.model.LocalizedText
import com.sazerat.languagelearning.core.domain.model.MILLIS_PER_DAY
import com.sazerat.languagelearning.core.domain.model.Rating
import com.sazerat.languagelearning.core.domain.model.ReadingText
import com.sazerat.languagelearning.core.domain.model.ReviewLogEntry
import com.sazerat.languagelearning.core.domain.model.Sentence
import com.sazerat.languagelearning.core.domain.model.TextId
import com.sazerat.languagelearning.core.domain.model.TextKind
import com.sazerat.languagelearning.core.domain.model.Token
import com.sazerat.languagelearning.core.domain.model.WordStatus
import com.sazerat.languagelearning.core.domain.repository.ReadingLogEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProgressTest {
    // Days are plain UTC days in these tests.
    private val clock = object : Clock {
        override fun now() = 10 * MILLIS_PER_DAY + 1

        override fun dayOf(millis: Long) = millis / MILLIS_PER_DAY
    }

    private fun at(day: Long) = day * MILLIS_PER_DAY + 1_000
    private fun review(day: Long) = ReviewLogEntry(LemmaId("ro:x"), Rating.GOOD, at(day))
    private fun reading(day: Long, minutes: Int) = ReadingLogEntry(TextId("t"), minutes, at(day))

    @Test
    fun `minutes count 15 seconds per review and the reading time of each text`() {
        val activity = Activity(reviews = List(8) { review(10) } + review(9), readings = listOf(reading(10, 3), reading(9, 5)))

        assertEquals(2 + 3, activity.minutesOn(10, clock))
    }

    @Test
    fun `the streak counts consecutive active days up to today`() {
        assertEquals(3, streak(setOf(8, 9, 10, 5), today = 10))
    }

    @Test
    fun `nothing done yet today does not break the streak`() {
        assertEquals(2, streak(setOf(8, 9), today = 10))
        assertEquals(0, streak(setOf(7, 8), today = 10))
    }

    @Test
    fun `active days come from both logs`() {
        val activity = Activity(reviews = listOf(review(3)), readings = listOf(reading(4, 1)))

        assertEquals(setOf(3L, 4L), activity.activeDays(clock))
    }

    @Test
    fun `known words grow from the day each became known`() {
        val words = listOf(
            LearnerWord(LemmaId("a"), WordStatus.KNOWN, at(2)),
            LearnerWord(LemmaId("b"), WordStatus.KNOWN, at(4)),
            LearnerWord(LemmaId("c"), WordStatus.LEARNING, at(1)),
        )

        assertEquals(listOf(0, 1, 1, 2), knownWordsByDay(words, 1L..4L, clock))
    }

    @Test
    fun `new words this week are the words added to the reviews in the last 7 days`() {
        val words = listOf(
            LearnerWord(LemmaId("a"), WordStatus.LEARNING, at(10)),
            LearnerWord(LemmaId("b"), WordStatus.LEARNING, at(4)),
            LearnerWord(LemmaId("c"), WordStatus.LEARNING, at(3)),
            LearnerWord(LemmaId("d"), WordStatus.KNOWN, at(10)),
        )

        assertEquals(2, newWordsThisWeek(words, today = 10, clock))
    }

    @Test
    fun `comprehension is given for the highest comfortable level`() {
        val known = mapOf(LemmaId("a") to WordStatus.KNOWN)
        val texts = listOf(text(CefrLevel.A1, "a", "a"), text(CefrLevel.A2, "a", "b"), text(CefrLevel.B1, "b", "b"))

        assertEquals(LevelComprehension(CefrLevel.A1, 100), levelComprehension(texts, known))
        assertEquals(LevelComprehension(CefrLevel.A1, 0), levelComprehension(texts, emptyMap()))
        assertNull(levelComprehension(emptyList(), known))
    }

    private fun text(level: CefrLevel, vararg lemmas: String) = ReadingText(
        id = TextId(level.name),
        languageCode = LanguageCode("ro-RO"),
        level = level,
        kind = TextKind.STORY,
        title = level.name,
        minutes = 1,
        sentences = listOf(Sentence(lemmas.map { Token(it, LemmaId(it)) }, LocalizedText.of(en = ""))),
    )
}
